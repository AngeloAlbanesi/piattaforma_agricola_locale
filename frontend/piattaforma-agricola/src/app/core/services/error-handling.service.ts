import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ErrorInfo, ErrorHandlingConfig } from '../models/error-handling.models';
import { ErrorFactory } from '../models/error-handling.models';


@Injectable({
  providedIn: 'root'
})
export class ErrorHandlingService {
  private config: ErrorHandlingConfig = {
    enableGlobalHandling: true,
    showNotifications: true,
    autoRetryAttempts: 3,
    retryDelay: 1000,
    enableLogging: true
  };

  private loadingStates = new Map<string, BehaviorSubject<boolean>>();
  private errorStates = new Map<string, BehaviorSubject<ErrorInfo | null>>();

  constructor(private snackBar: MatSnackBar) {}

  // Configurazione
  configure(config: Partial<ErrorHandlingConfig>): void {
    this.config = { ...this.config, ...config };
  }

  getConfig(): ErrorHandlingConfig {
    return { ...this.config };
  }

  // Gestione stati di caricamento
  setLoading(key: string, isLoading: boolean): void {
    if (!this.loadingStates.has(key)) {
      this.loadingStates.set(key, new BehaviorSubject<boolean>(false));
    }
    this.loadingStates.get(key)!.next(isLoading);
  }

  getLoading(key: string): Observable<boolean> {
    if (!this.loadingStates.has(key)) {
      this.loadingStates.set(key, new BehaviorSubject<boolean>(false));
    }
    return this.loadingStates.get(key)!.asObservable();
  }

  isLoading(key: string): boolean {
    return this.loadingStates.get(key)?.value || false;
  }

  // Gestione stati di errore
  setError(key: string, error: ErrorInfo | null): void {
    if (!this.errorStates.has(key)) {
      this.errorStates.set(key, new BehaviorSubject<ErrorInfo | null>(null));
    }
    this.errorStates.get(key)!.next(error);
    
    if (error && this.config.showNotifications) {
      this.showErrorNotification(error);
    }
  }

  getError(key: string): Observable<ErrorInfo | null> {
    if (!this.errorStates.has(key)) {
      this.errorStates.set(key, new BehaviorSubject<ErrorInfo | null>(null));
    }
    return this.errorStates.get(key)!.asObservable();
  }

  hasError(key: string): boolean {
    return this.errorStates.get(key)?.value !== null;
  }

  // Pulizia stati
  clearState(key: string): void {
    this.setLoading(key, false);
    this.setError(key, null);
  }

  clearAllStates(): void {
    this.loadingStates.clear();
    this.errorStates.clear();
  }

  // Conversione errori HTTP in ErrorInfo
  handleError(error: any, context?: string): ErrorInfo {
    if (error instanceof HttpErrorResponse) {
      return this.handleHttpError(error, context);
    }

    if (error instanceof Error) {
      return this.handleJavaScriptError(error, context);
    }

    return this.handleUnknownError(error, context);
  }

  private handleHttpError(error: HttpErrorResponse, context?: string): ErrorInfo {
    const statusCode = error.status;
    const message = error.error?.message || error.message || 'Errore HTTP';

    // Log dell'errore
    if (this.config.enableLogging) {
      console.error(`HTTP Error (${context || 'unknown'}):`, error);
    }

    // Conversione basata sul codice di stato
    if (statusCode === 0) {
      return ErrorFactory.createNetworkError(
        'Impossibile connettersi al server. Verifica la tua connessione internet.'
      );
    }

    if (statusCode === 404) {
      return ErrorFactory.createNotFoundError(
        error.error?.message || 'La risorsa richiesta non è stata trovata.'
      );
    }

    if (statusCode === 401 || statusCode === 403) {
      return ErrorFactory.createPermissionError(
        error.error?.message || 'Non hai i permessi per accedere a questa risorsa.'
      );
    }

    if (statusCode >= 400 && statusCode < 500) {
      return ErrorFactory.createValidationError(
        error.error?.message || 'I dati inviati non sono validi.'
      );
    }

    if (statusCode >= 500) {
      return ErrorFactory.createServerError(
        error.error?.message || 'Il server ha riscontrato un problema temporaneo.',
        statusCode
      );
    }

    return ErrorFactory.createUnknownError(message, {
      status: statusCode,
      url: error.url,
      context
    });
  }

  private handleJavaScriptError(error: Error, context?: string): ErrorInfo {
    if (this.config.enableLogging) {
      console.error(`JavaScript Error (${context || 'unknown'}):`, error);
    }

    return ErrorFactory.createUnknownError(error.message, {
      name: error.name,
      stack: error.stack,
      context
    });
  }

  private handleUnknownError(error: any, context?: string): ErrorInfo {
    if (this.config.enableLogging) {
      console.error(`Unknown Error (${context || 'unknown'}):`, error);
    }

    return ErrorFactory.createUnknownError(
      error?.message || 'Si è verificato un errore imprevisto.',
      {
        error,
        context
      }
    );
  }

  // Retry con backoff esponenziale
  retryWithBackoff<T>(
    operation: () => Observable<T>,
    maxAttempts: number = this.config.autoRetryAttempts,
    delay: number = this.config.retryDelay,
    context?: string
  ): Observable<T> {
    return new Observable<T>((observer) => {
      let attempts = 0;

      const attemptOperation = () => {
        attempts++;
        
        operation().subscribe({
          next: (result) => {
            observer.next(result);
            observer.complete();
          },
          error: (error) => {
            if (attempts < maxAttempts && this.shouldRetry(error)) {
              const retryDelay = delay * Math.pow(2, attempts - 1);
              
              if (this.config.enableLogging) {
                console.warn(`Retry attempt ${attempts}/${maxAttempts} for ${context || 'operation'} in ${retryDelay}ms`);
              }
              
              setTimeout(attemptOperation, retryDelay);
            } else {
              observer.error(this.handleError(error, context));
            }
          }
        });
      };

      attemptOperation();
    });
  }

  private shouldRetry(error: any): boolean {
    if (error instanceof HttpErrorResponse) {
      // Non retryare per errori client (4xx)
      if (error.status >= 400 && error.status < 500) {
        return false;
      }
      
      // Non retryare per errori specifici
      if (error.status === 401 || error.status === 403 || error.status === 404) {
        return false;
      }
    }
    
    return true;
  }

  // Notifiche
  private showErrorNotification(error: ErrorInfo): void {
    const config: MatSnackBarConfig = {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['error-notification']
    };

    this.snackBar.open(
      `${error.title}: ${error.message}`,
      'Chiudi',
      config
    );
  }

  showSuccessNotification(message: string, duration: number = 3000): void {
    const config: MatSnackBarConfig = {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['success-notification']
    };

    this.snackBar.open(message, 'Chiudi', config);
  }

  showInfoNotification(message: string, duration: number = 3000): void {
    const config: MatSnackBarConfig = {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['info-notification']
    };

    this.snackBar.open(message, 'Chiudi', config);
  }

  showWarningNotification(message: string, duration: number = 4000): void {
    const config: MatSnackBarConfig = {
      duration,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['warning-notification']
    };

    this.snackBar.open(message, 'Chiudi', config);
  }

  // Utilità per logging
  logInfo(message: string, data?: any): void {
    if (this.config.enableLogging) {
      console.log(`[INFO] ${message}`, data);
    }
  }

  logWarning(message: string, data?: any): void {
    if (this.config.enableLogging) {
      console.warn(`[WARNING] ${message}`, data);
    }
  }

  logError(message: string, error?: any): void {
    if (this.config.enableLogging) {
      console.error(`[ERROR] ${message}`, error);
    }
  }

  // Cleanup
  destroy(): void {
    this.clearAllStates();
  }
}