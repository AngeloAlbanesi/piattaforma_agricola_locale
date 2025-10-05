import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-accessible-image',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="accessible-image-container" [class.loading]="isLoading" [class.error]="hasError">
      <img
        [src]="sanitizedImageUrl"
        [alt]="alt"
        [title]="title"
        [attr.aria-label]="ariaLabel || alt"
        [attr.aria-describedby]="descriptionId"
        [attr.loading]="lazy ? 'lazy' : 'eager'"
        [attr.decoding]="decoding"
        [attr.width]="width"
        [attr.height]="height"
        [class.img-thumbnail]="thumbnail"
        [class.img-responsive]="responsive"
        (load)="onImageLoad()"
        (error)="onImageError()"
      />
      
      <!-- Loading skeleton -->
      <div *ngIf="isLoading" class="image-skeleton" aria-hidden="true"></div>
      
      <!-- Error fallback -->
      <div *ngIf="hasError" class="image-error" aria-live="polite">
        <div class="error-icon" aria-hidden="true">🖼️</div>
        <span class="error-text">Immagine non disponibile</span>
      </div>
      
      <!-- Description for screen readers -->
      <span *ngIf="description" [id]="descriptionId" class="sr-only">{{ description }}</span>
    </div>
  `,
    styles: [`
    .accessible-image-container {
      position: relative;
      display: inline-block;
      overflow: hidden;
      border-radius: var(--border-radius-sm, 4px);
      
      &.loading {
        background: #f5f5f5;
      }
      
      &.error {
        background: #fafafa;
        border: 1px solid #e0e0e0;
      }
    }
    
    img {
      display: block;
      max-width: 100%;
      height: auto;
      transition: opacity 0.3s ease;
      
      &.img-responsive {
        width: 100%;
        height: auto;
      }
      
      &.img-thumbnail {
        border-radius: var(--border-radius-sm, 4px);
        box-shadow: var(--shadow-sm, 0 2px 4px rgba(0, 0, 0, 0.1));
      }
      
      @media (prefers-reduced-motion: reduce) {
        transition: none;
      }
    }
    
    .image-skeleton {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: loading 1.5s infinite;
      
      @media (prefers-reduced-motion: reduce) {
        animation: none;
        background: #f0f0f0;
      }
    }
    
    .image-error {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: #fafafa;
      color: #666;
      padding: 16px;
      text-align: center;
      
      .error-icon {
        font-size: 24px;
        margin-bottom: 8px;
        opacity: 0.5;
      }
      
      .error-text {
        font-size: 14px;
        opacity: 0.7;
      }
    }
    
    .sr-only {
      position: absolute;
      width: 1px;
      height: 1px;
      padding: 0;
      margin: -1px;
      overflow: hidden;
      clip: rect(0, 0, 0, 0);
      white-space: nowrap;
      border: 0;
    }
    
    @keyframes loading {
      0% { background-position: 200% 0; }
      100% { background-position: -200% 0; }
    }
    
    @media (prefers-color-scheme: dark) {
      .image-skeleton {
        background: linear-gradient(90deg, #333 25%, #444 50%, #333 75%);
        
        @media (prefers-reduced-motion: reduce) {
          background: #333;
        }
      }
      
      .image-error {
        background: #2a2a2a;
        color: #ccc;
      }
    }
    
    @media (prefers-contrast: high) {
      .accessible-image-container {
        border: 1px solid currentColor;
      }
      
      .image-error {
        border: 2px solid currentColor;
      }
    }
  `]
})
export class AccessibleImageComponent implements OnInit, OnChanges {
    @Input() src: string = '';
    @Input() alt: string = '';
    @Input() title: string = '';
    @Input() ariaLabel: string = '';
    @Input() description: string = '';
    @Input() width?: string;
    @Input() height?: string;
    @Input() lazy: boolean = true;
    @Input() decoding: 'async' | 'sync' | 'auto' = 'async';
    @Input() thumbnail: boolean = false;
    @Input() responsive: boolean = true;
    @Input() fallbackUrl?: string;

    @Output() imageLoaded = new EventEmitter<void>();
    @Output() imageError = new EventEmitter<Error>();

    isLoading: boolean = false;
    hasError: boolean = false;
    sanitizedImageUrl: SafeUrl = '';
    descriptionId: string = '';

    constructor(private sanitizer: DomSanitizer) { }

    ngOnInit(): void {
        this.descriptionId = this.generateId();
        this.loadImage();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['src'] && !changes['src'].firstChange) {
            this.loadImage();
        }
    }

    private loadImage(): void {
        if (!this.src) {
            this.hasError = true;
            return;
        }

        this.isLoading = true;
        this.hasError = false;
        this.sanitizedImageUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.src);
    }

    onImageLoad(): void {
        this.isLoading = false;
        this.hasError = false;
        this.imageLoaded.emit();
    }

    onImageError(): void {
        this.isLoading = false;

        if (this.fallbackUrl && this.src !== this.fallbackUrl) {
            // Try fallback image
            this.src = this.fallbackUrl;
            this.sanitizedImageUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.fallbackUrl);
            this.isLoading = true;
        } else {
            this.hasError = true;
            const error = new Error(`Failed to load image: ${this.alt || 'unnamed image'}`);
            this.imageError.emit(error);
        }
    }

    private generateId(): string {
        return `img-desc-${Math.random().toString(36).substr(2, 9)}`;
    }
}