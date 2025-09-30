import { ChangeDetectionStrategy, Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule
  ]
})
export class LandingComponent implements OnInit, AfterViewInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.animateStats();
  }

  navigateToCatalog(): void {
    this.router.navigate(['/catalogo']);
  }

  navigateToAuth(): void {
    this.router.navigate(['/auth']);
  }

  scrollToSection(sectionId: string): void {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }

  private animateStats(): void {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const statNumbers = entry.target.querySelectorAll('.stat-number');
          statNumbers.forEach((stat: Element) => {
            const target = parseInt((stat as HTMLElement).getAttribute('data-target') || '0');
            const duration = 2000; // 2 seconds
            const increment = target / (duration / 16); // 60fps
            let current = 0;

            const timer = setInterval(() => {
              current += increment;
              if (current >= target) {
                current = target;
                clearInterval(timer);
              }
              (stat as HTMLElement).textContent = this.formatNumber(Math.floor(current));
            }, 16);
          });
          observer.unobserve(entry.target);
        }
      });
    });

    const statsSection = document.getElementById('stats');
    if (statsSection) {
      observer.observe(statsSection);
    }
  }

  private formatNumber(num: number): string {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M €';
    }
    return num.toString();
  }
}