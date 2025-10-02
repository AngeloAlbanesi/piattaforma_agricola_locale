import { ChangeDetectionStrategy, Component, OnInit, AfterViewInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

interface Product {
    id: string;
    name: string;
    category: string;
    description: string;
    origin: string;
    season: string;
    price: number;
    unit: string;
    certifications: string[];
    badge?: string;
}

@Component({
    selector: 'app-landing',
    templateUrl: './landing.component.html',
    styleUrls: ['./landing.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule
    ]
})
export class LandingComponent implements OnInit, AfterViewInit {

    // Product filtering properties
    products: Product[] = [
        {
            id: '1',
            name: 'Pomodori San Marzano DOP',
            category: 'vegetables',
            description: 'Pomodori dal sapore dolce e polposo, coltivati nel cuore del napoletano.',
            origin: 'Campania',
            season: 'Estate',
            price: 4.50,
            unit: 'kg',
            certifications: ['DOP', 'Organico'],
            badge: 'Bio'
        },
        {
            id: '2',
            name: 'Mele Golden di Montagna',
            category: 'fruits',
            description: 'Mele croccanti e succose, coltivate a 800 metri di altitudine.',
            origin: 'Trentino',
            season: 'Autunno',
            price: 3.20,
            unit: 'kg',
            certifications: ['Montagna', 'IGP'],
            badge: 'Novità'
        },
        {
            id: '3',
            name: 'Mozzarella di Bufala Campana',
            category: 'dairy',
            description: 'Formaggio fresco dal sapore inconfondibile, prodotto secondo la tradizione.',
            origin: 'Campania',
            season: 'Tutto l\'anno',
            price: 8.90,
            unit: 'kg',
            certifications: ['DOP', 'Artigianale'],
            badge: 'Artigianale'
        },
        {
            id: '4',
            name: 'Farina di Grano Antico',
            category: 'grains',
            description: 'Farina macinata a pietra da grani antichi non trattati geneticamente.',
            origin: 'Puglia',
            season: 'Tutto l\'anno',
            price: 4.80,
            unit: 'kg',
            certifications: ['Bio', 'Macinata a pietra'],
            badge: 'Antico'
        }
    ];

    filteredProducts: Product[] = [...this.products];
    activeFilter: string = 'all';

    constructor(private router: Router) { }

    ngOnInit(): void {
        this.initializeAnimations();
    }

    ngAfterViewInit(): void {
        this.animateStats();
    }

    // Navigation methods
    navigateToCatalog(): void {
        this.router.navigate(['/catalogo']);
    }

    navigateToLogin(): void {
        this.router.navigate(['/auth/login']);
    }

    navigateToRegister(): void {
        this.router.navigate(['/auth/register']);
    }

    scrollToSection(sectionId: string): void {
        const element = document.getElementById(sectionId);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth' });
        }
    }

    // Product filtering methods
    filterProducts(category: string): void {
        this.activeFilter = category;

        if (category === 'all') {
            this.filteredProducts = [...this.products];
        } else {
            this.filteredProducts = this.products.filter(product => product.category === category);
        }

        // Update button states
        this.updateFilterButtons();
    }

    private updateFilterButtons(): void {
        const filterButtons = document.querySelectorAll('.filter-btn');
        filterButtons.forEach(button => {
            button.classList.remove('active');
            if (button.textContent?.toLowerCase().includes(this.activeFilter) ||
                (this.activeFilter === 'all' && button.textContent?.toLowerCase().includes('tutti'))) {
                button.classList.add('active');
            }
        });
    }

    // Carousel methods
    previousProduct(): void {
        // Implement carousel previous logic
        console.log('Previous product');
    }

    nextProduct(): void {
        // Implement carousel next logic
        console.log('Next product');
    }

    // Traceability methods
    showStepDetails(stepNumber: number): void {
        console.log(`Show details for step ${stepNumber}`);
        // Implement step details modal or expanded view
    }

    openInteractiveMap(): void {
        console.log('Open interactive map');
        // Implement interactive map modal
    }

    // Testimonials methods
    openTestimonialForm(): void {
        console.log('Open testimonial form');
        // Implement testimonial form modal
    }

    // Animation methods
    private initializeAnimations(): void {
        // Initialize scroll animations for various elements
        this.observeElements('.value-card, .feature-item, .actor-card, .product-card, .testimonial-card');
    }

    private observeElements(selector: string): void {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate-in');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });

        document.querySelectorAll(selector).forEach(element => {
            observer.observe(element);
        });
    }

    private animateStats(): void {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateStatNumbers(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.5 });

        const statsSection = document.getElementById('stats');
        if (statsSection) {
            observer.observe(statsSection);
        }

        // Also animate testimonial stats
        const testimonialStats = document.querySelector('.testimonial-stats');
        if (testimonialStats) {
            observer.observe(testimonialStats);
        }
    }

    private animateStatNumbers(container: Element): void {
        const statNumbers = container.querySelectorAll('.stat-number[data-target]');

        statNumbers.forEach((stat: Element) => {
            const targetText = (stat as HTMLElement).getAttribute('data-target') || '0';
            let target: number;

            // Handle different formats like "150+", "500+", "50km", "4.9/5", "96%"
            if (targetText.includes('+')) {
                target = parseInt(targetText.replace('+', ''));
                this.animateNumber(stat as HTMLElement, target, '+');
            } else if (targetText.includes('km')) {
                target = parseInt(targetText.replace('km', ''));
                this.animateNumber(stat as HTMLElement, target, 'km');
            } else if (targetText.includes('/')) {
                // Handle rating format like "4.9/5"
                (stat as HTMLElement).textContent = targetText;
            } else if (targetText.includes('%')) {
                target = parseInt(targetText.replace('%', ''));
                this.animateNumber(stat as HTMLElement, target, '%');
            } else if (targetText.includes('€')) {
                target = parseInt(targetText.replace(/[^0-9]/g, ''));
                this.animateNumber(stat as HTMLElement, target, '€');
            } else {
                target = parseInt(targetText);
                this.animateNumber(stat as HTMLElement, target, '');
            }
        });
    }

    private animateNumber(element: HTMLElement, target: number, suffix: string): void {
        const duration = 2000; // 2 seconds
        const increment = target / (duration / 16); // 60fps
        let current = 0;

        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }

            if (suffix === '€') {
                element.textContent = this.formatNumber(Math.floor(current)) + suffix;
            } else {
                element.textContent = Math.floor(current).toString() + suffix;
            }
        }, 16);
    }

    private formatNumber(num: number): string {
        if (num >= 1000000) {
            return (num / 1000000).toFixed(1) + 'M';
        }
        if (num >= 1000) {
            return (num / 1000).toFixed(0) + 'K';
        }
        return num.toString();
    }
}