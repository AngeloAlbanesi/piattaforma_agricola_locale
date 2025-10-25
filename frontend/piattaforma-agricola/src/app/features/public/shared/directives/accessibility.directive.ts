import { Directive, Input, ElementRef, Renderer2, OnInit, OnDestroy } from '@angular/core';

@Directive({
  selector: '[appAriaLabel]',
  standalone: true
})
export class AriaLabelDirective implements OnInit {
  @Input('appAriaLabel') ariaLabel: string = '';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    if (this.ariaLabel) {
      this.renderer.setAttribute(this.el.nativeElement, 'aria-label', this.ariaLabel);
    }
  }
}

@Directive({
  selector: '[appAriaDescribedBy]',
  standalone: true
})
export class AriaDescribedByDirective implements OnInit {
  @Input('appAriaDescribedBy') ariaDescribedBy: string = '';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    if (this.ariaDescribedBy) {
      this.renderer.setAttribute(this.el.nativeElement, 'aria-describedby', this.ariaDescribedBy);
    }
  }
}

@Directive({
  selector: '[appAriaExpanded]',
  standalone: true
})
export class AriaExpandedDirective implements OnInit {
  @Input('appAriaExpanded') ariaExpanded: boolean = false;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.renderer.setAttribute(this.el.nativeElement, 'aria-expanded', this.ariaExpanded.toString());
  }
}

@Directive({
  selector: '[appAriaHidden]',
  standalone: true
})
export class AriaHiddenDirective implements OnInit {
  @Input('appAriaHidden') ariaHidden: boolean = false;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.renderer.setAttribute(this.el.nativeElement, 'aria-hidden', this.ariaHidden.toString());
  }
}

@Directive({
  selector: '[appAriaLive]',
  standalone: true
})
export class AriaLiveDirective implements OnInit {
  @Input('appAriaLive') ariaLive: 'polite' | 'assertive' | 'off' = 'polite';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.renderer.setAttribute(this.el.nativeElement, 'aria-live', this.ariaLive);
  }
}

@Directive({
  selector: '[appAriaBusy]',
  standalone: true
})
export class AriaBusyDirective implements OnInit, OnDestroy {
  @Input('appAriaBusy') ariaBusy: boolean = false;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.updateAriaBusy();
  }

  ngOnDestroy(): void {
    // Ensure aria-busy is false when directive is destroyed
    this.renderer.setAttribute(this.el.nativeElement, 'aria-busy', 'false');
  }

  private updateAriaBusy(): void {
    this.renderer.setAttribute(this.el.nativeElement, 'aria-busy', this.ariaBusy.toString());
  }
}

@Directive({
  selector: '[appRole]',
  standalone: true
})
export class RoleDirective implements OnInit {
  @Input('appRole') role: string = '';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    if (this.role) {
      this.renderer.setAttribute(this.el.nativeElement, 'role', this.role);
    }
  }
}

@Directive({
  selector: '[appTabIndex]',
  standalone: true
})
export class TabIndexDirective implements OnInit {
  @Input('appTabIndex') tabIndex: number = 0;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.renderer.setAttribute(this.el.nativeElement, 'tabindex', this.tabIndex.toString());
  }
}

@Directive({
  selector: '[appSkipLink]',
  standalone: true
})
export class SkipLinkDirective implements OnInit {
  @Input('appSkipLink') target: string = '';

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.renderer.setAttribute(this.el.nativeElement, 'href', `#${this.target}`);
    this.renderer.addClass(this.el.nativeElement, 'skip-link');
    
    // Add skip link styles
    this.renderer.setStyle(this.el.nativeElement, 'position', 'absolute');
    this.renderer.setStyle(this.el.nativeElement, 'top', '-40px');
    this.renderer.setStyle(this.el.nativeElement, 'left', '6px');
    this.renderer.setStyle(this.el.nativeElement, 'background', '#2196f3');
    this.renderer.setStyle(this.el.nativeElement, 'color', 'white');
    this.renderer.setStyle(this.el.nativeElement, 'padding', '8px');
    this.renderer.setStyle(this.el.nativeElement, 'text-decoration', 'none');
    this.renderer.setStyle(this.el.nativeElement, 'border-radius', '4px');
    this.renderer.setStyle(this.el.nativeElement, 'z-index', '1000');
    this.renderer.setStyle(this.el.nativeElement, 'transition', 'top 0.3s');
    
    // Show on focus
    this.renderer.listen(this.el.nativeElement, 'focus', () => {
      this.renderer.setStyle(this.el.nativeElement, 'top', '6px');
    });
    
    this.renderer.listen(this.el.nativeElement, 'blur', () => {
      this.renderer.setStyle(this.el.nativeElement, 'top', '-40px');
    });
  }
}

@Directive({
  selector: '[appFocusTrap]',
  standalone: true
})
export class FocusTrapDirective implements OnInit, OnDestroy {
  private previousActiveElement: Element | null = null;
  private keydownListener: (() => void) | null = null;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.trapFocus();
  }

  ngOnDestroy(): void {
    this.removeFocusTrap();
  }

  private trapFocus(): void {
    // Store the currently focused element
    this.previousActiveElement = document.activeElement;
    
    // Get all focusable elements within the container
    const focusableElements = this.el.nativeElement.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );

    if (focusableElements.length > 0) {
      const firstElement = focusableElements[0] as HTMLElement;
      const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement;

      // Focus the first element
      setTimeout(() => firstElement.focus(), 100);

      // Add keyboard event listener
      this.keydownListener = this.renderer.listen('document', 'keydown', (event: KeyboardEvent) => {
        if (event.key === 'Tab') {
          if (event.shiftKey) {
            // Shift + Tab
            if (document.activeElement === firstElement) {
              event.preventDefault();
              lastElement.focus();
            }
          } else {
            // Tab
            if (document.activeElement === lastElement) {
              event.preventDefault();
              firstElement.focus();
            }
          }
        } else if (event.key === 'Escape') {
          // Return focus to previous element
          this.removeFocusTrap();
        }
      });
    }
  }

  private removeFocusTrap(): void {
    // Remove event listener
    if (this.keydownListener) {
      this.keydownListener();
      this.keydownListener = null;
    }

    // Return focus to previous element
    if (this.previousActiveElement && this.previousActiveElement instanceof HTMLElement) {
      this.previousActiveElement.focus();
    }
  }
}

@Directive({
  selector: '[appAnnounceToScreenReader]',
  standalone: true
})
export class AnnounceToScreenReaderDirective implements OnInit {
  @Input('appAnnounceToScreenReader') message: string = '';
  @Input('appAnnouncePriority') priority: 'polite' | 'assertive' = 'polite';

  constructor() {}

  ngOnInit(): void {
    if (this.message) {
      this.announceToScreenReader(this.message, this.priority);
    }
  }

  private announceToScreenReader(message: string, priority: 'polite' | 'assertive' = 'polite'): void {
    const announcement = document.createElement('div');
    announcement.setAttribute('aria-live', priority);
    announcement.setAttribute('aria-atomic', 'true');
    announcement.className = 'sr-only';
    announcement.textContent = message;
    
    document.body.appendChild(announcement);
    
    // Remove after announcement
    setTimeout(() => {
      document.body.removeChild(announcement);
    }, 1000);
  }
}

@Directive({
  selector: '[appAccessibleImage]',
  standalone: true
})
export class AccessibleImageDirective implements OnInit {
  @Input('appAccessibleImage') alt: string = '';
  @Input('appImageDescription') description: string = '';
  @Input('appImageDecorative') decorative: boolean = false;

  constructor(
    private el: ElementRef,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    const img = this.el.nativeElement as HTMLImageElement;
    
    // Set alt text
    if (this.decorative) {
      this.renderer.setAttribute(img, 'alt', '');
      this.renderer.setAttribute(img, 'role', 'presentation');
    } else if (this.alt) {
      this.renderer.setAttribute(img, 'alt', this.alt);
    }
    
    // Add description if provided
    if (this.description) {
      const id = `img-desc-${Math.random().toString(36).substr(2, 9)}`;
      this.renderer.setAttribute(img, 'aria-describedby', id);
      
      // Create description element
      const descElement = this.renderer.createElement('span');
      this.renderer.setAttribute(descElement, 'id', id);
      this.renderer.addClass(descElement, 'sr-only');
      this.renderer.setProperty(descElement, 'textContent', this.description);
      
      // Insert after image
      img.parentNode?.insertBefore(descElement, img.nextSibling);
    }
    
    // Add error handling
    this.renderer.listen(img, 'error', () => {
      this.renderer.addClass(img, 'img-error');
      if (!this.decorative) {
        this.renderer.setAttribute(img, 'alt', this.alt || 'Immagine non disponibile');
      }
    });
  }
}

// Export all directives for easier importing
export const ACCESSIBILITY_DIRECTIVES = [
  AriaLabelDirective,
  AriaDescribedByDirective,
  AriaExpandedDirective,
  AriaHiddenDirective,
  AriaLiveDirective,
  AriaBusyDirective,
  RoleDirective,
  TabIndexDirective,
  SkipLinkDirective,
  FocusTrapDirective,
  AnnounceToScreenReaderDirective,
  AccessibleImageDirective
];