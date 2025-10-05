export interface ErrorInfo {
  title: string;
  message: string;
  type: 'network' | 'server' | 'validation' | 'not_found' | 'permission' | 'unknown';
  statusCode?: number;
  details?: any;
  retryable?: boolean;
}

export interface LoadingState {
  message: string;
  showProgress?: boolean;
  progress?: number;
}

export interface ErrorHandlingConfig {
  enableGlobalHandling: boolean;
  showNotifications: boolean;
  autoRetryAttempts: number;
  retryDelay: number;
  enableLogging: boolean;
}

// Factory methods per creare errori standard
export class ErrorFactory {
  static createNetworkError(message?: string): ErrorInfo {
    return {
      title: 'Errore di connessione',
      message: message || 'Impossibile connettersi al server. Verifica la tua connessione internet.',
      type: 'network',
      retryable: true
    };
  }

  static createServerError(message?: string, statusCode?: number): ErrorInfo {
    return {
      title: 'Errore del server',
      message: message || 'Il server ha riscontrato un problema. Riprova più tardi.',
      type: 'server',
      statusCode,
      retryable: true
    };
  }

  static createNotFoundError(message?: string): ErrorInfo {
    return {
      title: 'Contenuto non trovato',
      message: message || 'Il contenuto richiesto non è disponibile.',
      type: 'not_found',
      retryable: false
    };
  }

  static createPermissionError(message?: string): ErrorInfo {
    return {
      title: 'Accesso negato',
      message: message || 'Non hai i permessi per accedere a questa risorsa.',
      type: 'permission',
      retryable: false
    };
  }

  static createValidationError(message?: string): ErrorInfo {
    return {
      title: 'Dati non validi',
      message: message || 'I dati inseriti non sono validi. Controlla e riprova.',
      type: 'validation',
      retryable: false
    };
  }

  static createUnknownError(message?: string, details?: any): ErrorInfo {
    return {
      title: 'Errore imprevisto',
      message: message || 'Si è verificato un errore imprevisto. Riprova più tardi.',
      type: 'unknown',
      details,
      retryable: true
    };
  }
}