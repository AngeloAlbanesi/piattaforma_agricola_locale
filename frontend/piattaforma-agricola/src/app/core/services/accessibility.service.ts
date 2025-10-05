import { Injectable, ElementRef, Renderer2 } from '@angular/core';
import { BehaviorSubject, Observable, fromEvent, merge } from 'rxjs';
import { map, distinctUntilChanged } from 'rxjs/operators';

export interface AccessibilitySettings {
  reducedMotion: boolean;
  highContrast: boolean;
  darkMode: boolean;
  fontSize: 'small' | 'medium' | 'large';
  screenReaderOnly: boolean;
}

export interface AriaAttributes {
  'aria-label'?: string;
  'aria-labelledby'?: string;
  'aria-describedby'?: string;
  'aria-expanded'?: boolean;
  'aria-hidden'?: boolean;
  'aria-live'?: 'polite' | 'assertive' | 'off';
  'aria-atomic'?: boolean;
  'aria-busy'?: boolean;
  'aria-current'?: string | boolean;
  'aria-disabled'?: boolean;
  'aria-pressed'?: boolean;
  'aria-selected'?: boolean;
  'role'?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AccessibilityService {
  private settingsSubject = new BehaviorSubject<AccessibilitySettings>({
    reducedMotion: false,
    highContrast: false,
    darkMode: false,
    fontSize: 'medium',
    screenReaderOnly: false
  });

  public settings$ = this.settingsSubject.asObservable();

  constructor() {
    this.detectSystemPreferences();
    this.setupKeyboardNavigation();
    this.setupFocusManagement();
  }

  // Get current settings
  get settings(): AccessibilitySettings {
    return this.settingsSubject.value;
  }

  // Update settings
  updateSettings(newSettings: Partial<AccessibilitySettings>): void {
    const currentSettings = this.settingsSubject.value;
    this.settingsSubject.next({ ...currentSettings, ...newSettings });
    this.applySettingsToDocument();
  }

  // Observable for specific setting
  getSetting$(setting: keyof AccessibilitySettings): Observable<any> {
    return this.settings$.pipe(
      map(settings => settings[setting]),
      distinctUntilChanged()
    );
  }

  // Detect system preferences
  private detectSystemPreferences(): void {
    if (typeof window !== 'undefined') {
      // Detect reduced motion preference
      const reducedMotionQuery = window.matchMedia('(prefers-reduced-motion: reduce)');
      this.updateSettingFromMediaQuery('reducedMotion', reducedMotionQuery);
      
      // Detect high contrast preference
      const highContrastQuery = window.matchMedia('(prefers-contrast: high)');
      this.updateSettingFromMediaQuery('highContrast', highContrastQuery);
      
      // Detect dark mode preference
      const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');
      this.updateSettingFromMediaQuery('darkMode', darkModeQuery);
    }
  }

  // Update setting from media query
  private updateSettingFromMediaQuery(setting: keyof AccessibilitySettings, mediaQuery: MediaQueryList): void {
    const updateValue = () => {
      this.updateSettings({ [setting]: mediaQuery.matches });
    };

    updateValue(); // Initial value
    mediaQuery.addEventListener('change', updateValue);
  }

  // Apply settings to document
  private applySettingsToDocument(): void {
    const settings = this.settings;
    const documentElement = document.documentElement;

    // Apply classes to document
    documentElement.classList.toggle('reduced-motion', settings.reducedMotion);
    documentElement.classList.toggle('high-contrast', settings.highContrast);
    documentElement.classList.toggle('dark-mode', settings.darkMode);
    documentElement.classList.toggle('screen-reader-only', settings.screenReaderOnly);
    documentElement.classList.remove('font-size-small', 'font-size-medium', 'font-size-large');
    documentElement.classList.add(`font-size-${settings.fontSize}`);

    // Apply CSS custom properties
    documentElement.style.setProperty('--font-size-multiplier', this.getFontSizeMultiplier(settings.fontSize).toString());
  }

  // Get font size multiplier
  private getFontSizeMultiplier(fontSize: string): number {
    switch (fontSize) {
      case 'small': return 0.875;
      case 'large': return 1.125;
      default: return 1;
    }
  }

  // Setup keyboard navigation
  private setupKeyboardNavigation(): void {
    if (typeof document !== 'undefined') {
      // Skip links functionality
      this.addSkipLinks();
      
      // Focus trap for modals
      this.setupFocusTrap();
      
      // Keyboard navigation for menus
      this.setupMenuNavigation();
    }
  }

  // Add skip links
  private addSkipLinks(): void {
    const skipLinksHtml = `
      <div class="skip-links">
        <a href="#main-content">Vai al contenuto principale</a>
        <a href="#navigation">Vai alla navigazione</a>
        <a href="#search">Vai alla ricerca</a>
      </div>
    `;
    
    const skipLinksContainer = document.createElement('div');
    skipLinksContainer.innerHTML = skipLinksHtml;
    if (skipLinksContainer.firstChild) {
      document.body.insertBefore(skipLinksContainer.firstChild, document.body.firstChild);
    }
  }

  // Setup focus trap for modals
  private setupFocusTrap(): void {
    // This would be used when modals are opened
    // Implementation depends on modal system used
  }

  // Setup menu navigation
  private setupMenuNavigation(): void {
    // Enhanced keyboard navigation for dropdown menus
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape') {
        // Close any open menus/modals
        this.closeAllMenus();
      }
    });
  }

  // Close all menus
  private closeAllMenus(): void {
    // Implementation depends on menu system used
    const openMenus = document.querySelectorAll('[aria-expanded="true"]');
    openMenus.forEach(menu => {
      menu.setAttribute('aria-expanded', 'false');
    });
  }

  // Setup focus management
  private setupFocusManagement(): void {
    // Add focus styles
    this.addFocusStyles();
    
    // Manage focus for dynamic content
    this.setupFocusForDynamicContent();
  }

  // Add focus styles
  private addFocusStyles(): void {
    const style = document.createElement('style');
    style.textContent = `
      /* Enhanced focus styles */
      :focus {
        outline: 2px solid #2196f3 !important;
        outline-offset: 2px !important;
      }
      
      /* High contrast focus styles */
      @media (prefers-contrast: high) {
        :focus {
          outline: 3px solid currentColor !important;
          outline-offset: 2px !important;
        }
      }
      
      /* Reduced motion focus styles */
      @media (prefers-reduced-motion: reduce) {
        * {
          transition: none !important;
          animation: none !important;
        }
      }
    `;
    document.head.appendChild(style);
  }

  // Setup focus for dynamic content
  private setupFocusForDynamicContent(): void {
    // Observe DOM changes for dynamic content
    if (typeof MutationObserver !== 'undefined') {
      const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
          if (mutation.type === 'childList') {
            mutation.addedNodes.forEach((node) => {
              if (node.nodeType === Node.ELEMENT_NODE) {
                const element = node as Element;
                
                // Auto-focus elements with autofocus attribute
                if (element.hasAttribute('autofocus')) {
                  setTimeout(() => (element as HTMLElement).focus(), 100);
                }
                
                // Add ARIA attributes to interactive elements
                this.enhanceAccessibility(element);
              }
            });
          }
        });
      });

      observer.observe(document.body, {
        childList: true,
        subtree: true
      });
    }
  }

  // Enhance accessibility for elements
  private enhanceAccessibility(element: Element): void {
    // Add appropriate ARIA attributes based on element type
    const tagName = element.tagName.toLowerCase();
    
    switch (tagName) {
      case 'button':
        if (!element.hasAttribute('type')) {
          element.setAttribute('type', 'button');
        }
        break;
        
      case 'img':
        if (!element.hasAttribute('alt')) {
          element.setAttribute('alt', ''); // Empty alt for decorative images
        }
        break;
        
      case 'input':
        this.enhanceInputAccessibility(element);
        break;
        
      case 'a':
        this.enhanceLinkAccessibility(element);
        break;
    }
  }

  // Enhance input accessibility
  private enhanceInputAccessibility(input: Element): void {
    const inputElement = input as HTMLInputElement;
    
    // Add aria-describedby if there's a description
    const id = inputElement.id;
    if (id) {
      const description = document.querySelector(`[for="${id}"]`);
      if (description) {
        const descriptionId = `${id}-desc`;
        description.id = descriptionId;
        inputElement.setAttribute('aria-describedby', descriptionId);
      }
    }
    
    // Add appropriate input mode for mobile keyboards
    switch (inputElement.type) {
      case 'tel':
        inputElement.setAttribute('inputmode', 'tel');
        break;
      case 'email':
        inputElement.setAttribute('inputmode', 'email');
        break;
      case 'url':
        inputElement.setAttribute('inputmode', 'url');
        break;
      case 'number':
        inputElement.setAttribute('inputmode', 'numeric');
        break;
    }
  }

  // Enhance link accessibility
  private enhanceLinkAccessibility(link: Element): void {
    const linkElement = link as HTMLAnchorElement;
    
    // Add aria-label if link text is not descriptive
    if (linkElement.textContent && linkElement.textContent.length < 3) {
      const href = linkElement.getAttribute('href');
      if (href) {
        linkElement.setAttribute('aria-label', `Link to ${href}`);
      }
    }
    
    // Add external link indicator
    if (linkElement.hostname !== window.location.hostname) {
      linkElement.setAttribute('aria-label', 
        `${linkElement.getAttribute('aria-label') || linkElement.textContent} (opens in new window)`
      );
      linkElement.setAttribute('target', '_blank');
      linkElement.setAttribute('rel', 'noopener noreferrer');
    }
  }

  // Utility methods for ARIA attributes
  setAriaAttributes(element: ElementRef, attributes: AriaAttributes): void {
    Object.entries(attributes).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        this.setAriaAttribute(element, key, value.toString());
      }
    });
  }

  private setAriaAttribute(element: ElementRef, attribute: string, value: string): void {
    element.nativeElement.setAttribute(attribute, value);
  }

  // Announce messages to screen readers
  announce(message: string, priority: 'polite' | 'assertive' = 'polite'): void {
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

  // Focus management
  focusElement(element: ElementRef): void {
    if (element && element.nativeElement) {
      element.nativeElement.focus();
    }
  }

  trapFocus(container: ElementRef): void {
    if (!container || !container.nativeElement) return;

    const focusableElements = container.nativeElement.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );

    if (focusableElements.length > 0) {
      const firstElement = focusableElements[0] as HTMLElement;
      const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement;

      const handleTabKey = (e: KeyboardEvent) => {
        if (e.key === 'Tab') {
          if (e.shiftKey) {
            if (document.activeElement === firstElement) {
              lastElement.focus();
              e.preventDefault();
            }
          } else {
            if (document.activeElement === lastElement) {
              firstElement.focus();
              e.preventDefault();
            }
          }
        }
      };

      container.nativeElement.addEventListener('keydown', handleTabKey);
      firstElement.focus();
    }
  }

  // Color contrast checker
  checkColorContrast(foreground: string, background: string): number {
    // This is a simplified version - in production, use a proper color contrast library
    const getLuminance = (color: string): number => {
      const rgb = this.hexToRgb(color);
      if (!rgb) return 0;
      
      const [r, g, b] = [rgb.r, rgb.g, rgb.b].map(val => {
        val = val / 255;
        return val <= 0.03928 ? val / 12.92 : Math.pow((val + 0.055) / 1.055, 2.4);
      });
      
      return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    };

    const lum1 = getLuminance(foreground);
    const lum2 = getLuminance(background);
    const brightest = Math.max(lum1, lum2);
    const darkest = Math.min(lum1, lum2);

    return (brightest + 0.05) / (darkest + 0.05);
  }

  private hexToRgb(hex: string): { r: number; g: number; b: number } | null {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
      r: parseInt(result[1], 16),
      g: parseInt(result[2], 16),
      b: parseInt(result[3], 16)
    } : null;
  }

  // Screen reader detection
  detectScreenReader(): boolean {
    // This is a heuristic approach - not 100% reliable
    const testElement = document.createElement('div');
    testElement.setAttribute('aria-hidden', 'true');
    testElement.innerHTML = 'Screen reader test';
    testElement.style.position = 'absolute';
    testElement.style.left = '-9999px';
    document.body.appendChild(testElement);
    
    const isDetected = testElement.offsetHeight === 0;
    document.body.removeChild(testElement);
    
    return isDetected;
  }
}