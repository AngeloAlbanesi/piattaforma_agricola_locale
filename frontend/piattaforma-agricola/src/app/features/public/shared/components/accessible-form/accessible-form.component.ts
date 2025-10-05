import { Component, Input, Output, EventEmitter, ContentChildren, QueryList, AfterContentInit } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface AccessibleFieldConfig {
    id: string;
    label: string;
    type: 'text' | 'email' | 'tel' | 'password' | 'number' | 'search' | 'url' | 'date' | 'time' | 'datetime-local';
    placeholder?: string;
    required?: boolean;
    disabled?: boolean;
    readonly?: boolean;
    autocomplete?: string;
    minlength?: number;
    maxlength?: number;
    min?: number;
    max?: number;
    step?: number;
    pattern?: string;
    description?: string;
    errorMessage?: string;
    value?: any;
    options?: Array<{ value: string; label: string; disabled?: boolean }>;
}

@Component({
    selector: 'app-accessible-form',
    standalone: true,
    imports: [CommonModule],
    template: `
    <form 
      class="accessible-form"
      [attr.aria-labelledby]="titleId"
      [attr.aria-describedby]="descriptionId"
      (submit)="onSubmit($event)"
      novalidate
    >
      <!-- Form title -->
      <h2 *ngIf="title" [id]="titleId" class="form-title">{{ title }}</h2>
      
      <!-- Form description -->
      <p *ngIf="description" [id]="descriptionId" class="form-description">{{ description }}</p>
      
      <!-- Required fields notice -->
      <p *ngIf="hasRequiredFields" class="required-notice">
        <span class="required-indicator" aria-hidden="true">*</span>
        Campi obbligatori
      </p>
      
      <!-- Form fields -->
      <div class="form-fields">
        <ng-content></ng-content>
      </div>
      
      <!-- Form actions -->
      <div class="form-actions" role="group" aria-label="Azioni modulo">
        <button 
          type="submit"
          class="btn btn-primary"
          [disabled]="isSubmitting || !isValid"
          [attr.aria-describedby]="submitErrorId"
        >
          <span *ngIf="isSubmitting" class="loading-spinner" aria-hidden="true"></span>
          {{ isSubmitting ? submitLoadingText : submitText }}
        </button>
        
        <button 
          *ngIf="showReset"
          type="button"
          class="btn btn-secondary"
          (click)="onReset()"
          [disabled]="isSubmitting"
        >
          {{ resetText }}
        </button>
        
        <button 
          *ngIf="showCancel"
          type="button"
          class="btn btn-tertiary"
          (click)="onCancel()"
          [disabled]="isSubmitting"
        >
          {{ cancelText }}
        </button>
      </div>
      
      <!-- Submit error message -->
      <div 
        *ngIf="submitError" 
        [id]="submitErrorId"
        class="form-error submit-error"
        role="alert"
        aria-live="assertive"
      >
        {{ submitError }}
      </div>
      
      <!-- Success message -->
      <div 
        *ngIf="successMessage" 
        class="form-success"
        role="status"
        aria-live="polite"
      >
        {{ successMessage }}
      </div>
    </form>
  `,
    styles: [`
    .accessible-form {
      max-width: 600px;
      margin: 0 auto;
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }
    
    .form-title {
      margin: 0 0 16px 0;
      font-size: 1.5rem;
      font-weight: 600;
      color: #333;
    }
    
    .form-description {
      margin: 0 0 24px 0;
      color: #666;
      line-height: 1.5;
    }
    
    .required-notice {
      margin: 0 0 24px 0;
      font-size: 0.875rem;
      color: #666;
    }
    
    .required-indicator {
      color: #f44336;
      margin-right: 4px;
    }
    
    .form-fields {
      margin-bottom: 24px;
    }
    
    .form-actions {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      
      @media (max-width: 768px) {
        flex-direction: column;
        
        button {
          width: 100%;
        }
      }
    }
    
    .btn {
      padding: 12px 24px;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
      min-height: 44px; // Touch target size
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      
      &:focus {
        outline: 2px solid #2196f3;
        outline-offset: 2px;
      }
      
      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
      
      @media (prefers-reduced-motion: reduce) {
        transition: none;
      }
    }
    
    .btn-primary {
      background-color: #2196f3;
      color: white;
      
      &:hover:not(:disabled) {
        background-color: #1976d2;
      }
    }
    
    .btn-secondary {
      background-color: #f5f5f5;
      color: #333;
      border: 1px solid #ddd;
      
      &:hover:not(:disabled) {
        background-color: #e0e0e0;
      }
    }
    
    .btn-tertiary {
      background-color: transparent;
      color: #666;
      border: 1px solid transparent;
      
      &:hover:not(:disabled) {
        background-color: #f5f5f5;
        border-color: #ddd;
      }
    }
    
    .loading-spinner {
      width: 16px;
      height: 16px;
      border: 2px solid transparent;
      border-top: 2px solid currentColor;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      
      @media (prefers-reduced-motion: reduce) {
        animation: none;
      }
    }
    
    .form-error {
      color: #f44336;
      background-color: #ffebee;
      padding: 12px;
      border-radius: 4px;
      margin: 16px 0;
      border-left: 4px solid #f44336;
    }
    
    .form-success {
      color: #4caf50;
      background-color: #e8f5e8;
      padding: 12px;
      border-radius: 4px;
      margin: 16px 0;
      border-left: 4px solid #4caf50;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    @media (prefers-color-scheme: dark) {
      .accessible-form {
        background: #1e1e1e;
        color: #ffffff;
      }
      
      .form-title {
        color: #ffffff;
      }
      
      .form-description {
        color: #cccccc;
      }
      
      .required-notice {
        color: #cccccc;
      }
      
      .btn-secondary {
        background-color: #333;
        color: #ffffff;
        border-color: #555;
        
        &:hover:not(:disabled) {
          background-color: #444;
        }
      }
      
      .btn-tertiary {
        color: #cccccc;
        
        &:hover:not(:disabled) {
          background-color: #333;
          border-color: #555;
        }
      }
      
      .form-error {
        background-color: rgba(244, 67, 54, 0.1);
        border-color: #f44336;
      }
      
      .form-success {
        background-color: rgba(76, 175, 80, 0.1);
        border-color: #4caf50;
      }
    }
    
    @media (prefers-contrast: high) {
      .accessible-form {
        border: 2px solid currentColor;
      }
      
      .btn {
        border: 2px solid currentColor;
      }
    }
  `]
})
export class AccessibleFormComponent implements AfterContentInit {
    @Input() title: string = '';
    @Input() description: string = '';
    @Input() submitText: string = 'Invia';
    @Input() submitLoadingText: string = 'Invio in corso...';
    @Input() resetText: string = 'Resetta';
    @Input() cancelText: string = 'Annulla';
    @Input() showReset: boolean = false;
    @Input() showCancel: boolean = false;
    @Input() isSubmitting: boolean = false;
    @Input() isValid: boolean = true;
    @Input() submitError: string = '';
    @Input() successMessage: string = '';
    @Input() hasRequiredFields: boolean = false;

    @Output() formSubmit = new EventEmitter<Event>();
    @Output() formReset = new EventEmitter<void>();
    @Output() formCancel = new EventEmitter<void>();

    @ContentChildren('formField') formFields!: QueryList<any>;

    titleId: string = '';
    descriptionId: string = '';
    submitErrorId: string = '';

    constructor() {
        this.generateIds();
    }

    ngAfterContentInit(): void {
        // Auto-focus first field if form has title
        if (this.title && this.formFields?.length) {
            setTimeout(() => {
                const firstField = this.formFields.first;
                if (firstField?.nativeElement) {
                    firstField.nativeElement.focus();
                }
            }, 100);
        }
    }

    onSubmit(event: Event): void {
        event.preventDefault();
        if (!this.isSubmitting && this.isValid) {
            this.formSubmit.emit(event);
        }
    }

    onReset(): void {
        this.formReset.emit();
    }

    onCancel(): void {
        this.formCancel.emit();
    }

    private generateIds(): void {
        const timestamp = Date.now();
        const random = Math.random().toString(36).substr(2, 9);

        this.titleId = `form-title-${timestamp}-${random}`;
        this.descriptionId = `form-desc-${timestamp}-${random}`;
        this.submitErrorId = `form-error-${timestamp}-${random}`;
    }
}