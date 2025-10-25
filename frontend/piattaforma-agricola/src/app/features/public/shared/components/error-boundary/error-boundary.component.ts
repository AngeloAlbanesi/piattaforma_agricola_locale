import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { ErrorInfo, LoadingState, ErrorFactory } from '../../../../../core/models/error-handling.models';

@Component({
  selector: 'app-error-boundary',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatExpansionModule
  ],
  templateUrl: './error-boundary.component.html',
  styleUrls: ['./error-boundary.component.scss']
})
export class ErrorBoundaryComponent implements OnInit {
  @Input() isLoading: boolean = false;
  @Input() error: ErrorInfo | null = null;
  @Input() loadingState: LoadingState | null = null;
  @Input() showRetry: boolean = true;
  @Input() compact: boolean = false;
  @Input() customMessage: string | null = null;
  
  @Output() retry = new EventEmitter<void>();
  @Output() dismiss = new EventEmitter<void>();

  defaultLoadingMessage = 'Caricamento in corso...';
  defaultErrorMessage = 'Si è verificato un errore imprevisto.';

  constructor(private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    // Auto-dismiss error after 10 seconds if not critical
    if (this.error && !this.isCriticalError(this.error)) {
      setTimeout(() => {
        this.dismiss.emit();
      }, 10000);
    }
  }

  getLoadingMessage(): string {
    if (this.customMessage) return this.customMessage;
    if (this.loadingState?.message) return this.loadingState.message;
    return this.defaultLoadingMessage;
  }

  getErrorTitle(): string {
    if (!this.error) return this.defaultErrorMessage;
    return this.error.title || this.defaultErrorMessage;
  }

  getErrorMessage(): string {
    if (!this.error) return '';
    return this.error.message || '';
  }

  getErrorIcon(): string {
    if (!this.error) return 'error_outline';
    
    const iconMap: Record<string, string> = {
      'network': 'wifi_off',
      'server': 'cloud_off',
      'validation': 'warning',
      'not_found': 'search_off',
      'permission': 'lock',
      'unknown': 'error_outline'
    };
    
    return iconMap[this.error.type] || 'error_outline';
  }

  getErrorColor(): string {
    if (!this.error) return '#f44336'; // red
    
    const colorMap: Record<string, string> = {
      'network': '#ff9800', // orange
      'server': '#f44336',   // red
      'validation': '#ff9800', // orange
      'not_found': '#9e9e9e', // grey
      'permission': '#f44336', // red
      'unknown': '#f44336'     // red
    };
    
    return colorMap[this.error.type] || '#f44336';
  }

  isRetryable(): boolean {
    if (!this.showRetry || !this.error) return false;
    return this.error.retryable !== false;
  }

  isCriticalError(error: ErrorInfo): boolean {
    return error.type === 'permission' ||
           (error.statusCode !== undefined && error.statusCode >= 500);
  }

  onRetry(): void {
    this.retry.emit();
    this.showRetryFeedback();
  }

  onDismiss(): void {
    this.dismiss.emit();
  }

  private showRetryFeedback(): void {
    this.snackBar.open('Tentativo di recupero in corso...', 'Chiudi', {
      duration: 2000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }

  // Metodi per tipi di errore specifici (deprecati, usare ErrorFactory)
  static createNetworkError(message?: string): ErrorInfo {
    return ErrorFactory.createNetworkError(message);
  }

  static createServerError(message?: string, statusCode?: number): ErrorInfo {
    return ErrorFactory.createServerError(message, statusCode);
  }

  static createNotFoundError(message?: string): ErrorInfo {
    return ErrorFactory.createNotFoundError(message);
  }

  static createPermissionError(message?: string): ErrorInfo {
    return ErrorFactory.createPermissionError(message);
  }

  static createValidationError(message?: string): ErrorInfo {
    return ErrorFactory.createValidationError(message);
  }

  static createUnknownError(message?: string, details?: any): ErrorInfo {
    return ErrorFactory.createUnknownError(message, details);
  }
}