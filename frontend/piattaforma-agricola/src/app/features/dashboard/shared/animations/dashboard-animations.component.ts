import { Injectable } from '@angular/core';
import { AnimationTriggerMetadata, animate, state, style, transition, trigger } from '@angular/animations';

@Injectable({
  providedIn: 'root'
})
export class DashboardAnimationsService {

  // Animation per le cards delle statistiche
  static statsCardAnimation: AnimationTriggerMetadata = trigger('statsCardAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateY(20px)'
    })),
    transition('void => *', [
      animate('0.5s ease-out', style({
        opacity: 1,
        transform: 'translateY(0)'
      }))
    ])
  ]);

  // Animation per le quick actions
  static quickActionsAnimation: AnimationTriggerMetadata = trigger('quickActionsAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'scale(0.8)'
    })),
    transition('void => *', [
      animate('0.3s ease-out', style({
        opacity: 1,
        transform: 'scale(1)'
      }))
    ])
  ]);

  // Animation per le tabs
  static tabsAnimation: AnimationTriggerMetadata = trigger('tabsAnimation', [
    transition(':enter', [
      style({ opacity: 0, transform: 'translateX(-10px)' }),
      animate('0.3s ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
    ]),
    transition(':leave', [
      style({ opacity: 1, transform: 'translateX(0)' }),
      animate('0.3s ease-out', style({ opacity: 0, transform: 'translateX(10px)' }))
    ])
  ]);

  // Animation per le progress bars
  static progressBarAnimation: AnimationTriggerMetadata = trigger('progressBarAnimation', [
    state('void', style({
      width: '0%'
    })),
    transition('void => *', [
      animate('1s ease-out', style({
        width: '*'
      }))
    ])
  ]);

  // Animation per i badge
  static badgeAnimation: AnimationTriggerMetadata = trigger('badgeAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'scale(0)'
    })),
    transition('void => *', [
      animate('0.2s ease-out', style({
        opacity: 1,
        transform: 'scale(1)'
      }))
    ])
  ]);

  // Animation per i tooltip
  static tooltipAnimation: AnimationTriggerMetadata = trigger('tooltipAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateY(-5px)'
    })),
    transition('void => *', [
      animate('0.2s ease-out', style({
        opacity: 1,
        transform: 'translateY(0)'
      }))
    ])
  ]);

  // Animation per i loading spinner
  static loadingAnimation: AnimationTriggerMetadata = trigger('loadingAnimation', [
    state('void', style({
      opacity: 0
    })),
    transition('void => *', [
      animate('0.3s ease-in', style({
        opacity: 1
      }))
    ])
  ]);

  // Animation per gli errori
  static errorAnimation: AnimationTriggerMetadata = trigger('errorAnimation', [
    transition(':enter', [
      style({ transform: 'translateX(100%)' }),
      animate('0.3s ease-out', style({ transform: 'translateX(0)' }))
    ]),
    transition(':leave', [
      animate('0.3s ease-in', style({ transform: 'translateX(100%)' }))
    ])
  ]);

  // Animation per i success
  static successAnimation: AnimationTriggerMetadata = trigger('successAnimation', [
    transition(':enter', [
      style({ transform: 'translateY(-20px)', opacity: 0 }),
      animate('0.3s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
    ]),
    transition(':leave', [
      animate('0.2s ease-in', style({ transform: 'translateY(-20px)', opacity: 0 }))
    ])
  ]);

  // Animation per i fade in/out
  static fadeInOutAnimation: AnimationTriggerMetadata = trigger('fadeInOutAnimation', [
    state('visible', style({
      opacity: 1
    })),
    state('hidden', style({
      opacity: 0
    })),
    transition('visible => hidden', [
      animate('0.3s ease-out')
    ]),
    transition('hidden => visible', [
      animate('0.3s ease-in')
    ])
  ]);

  // Animation per lo slide in da sinistra
  static slideInLeftAnimation: AnimationTriggerMetadata = trigger('slideInLeftAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateX(-100%)'
    })),
    transition('void => *', [
      animate('0.5s ease-out', style({
        opacity: 1,
        transform: 'translateX(0)'
      }))
    ])
  ]);

  // Animation per lo slide in da destra
  static slideInRightAnimation: AnimationTriggerMetadata = trigger('slideInRightAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateX(100%)'
    })),
    transition('void => *', [
      animate('0.5s ease-out', style({
        opacity: 1,
        transform: 'translateX(0)'
      }))
    ])
  ]);

  // Animation per lo slide in dall'alto
  static slideInTopAnimation: AnimationTriggerMetadata = trigger('slideInTopAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateY(-100%)'
    })),
    transition('void => *', [
      animate('0.5s ease-out', style({
        opacity: 1,
        transform: 'translateY(0)'
      }))
    ])
  ]);

  // Animation per lo slide in dal basso
  static slideInBottomAnimation: AnimationTriggerMetadata = trigger('slideInBottomAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateY(100%)'
    })),
    transition('void => *', [
      animate('0.5s ease-out', style({
        opacity: 1,
        transform: 'translateY(0)'
      }))
    ])
  ]);

  // Animation per lo zoom in
  static zoomInAnimation: AnimationTriggerMetadata = trigger('zoomInAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'scale(0.8)'
    })),
    transition('void => *', [
      animate('0.3s ease-out', style({
        opacity: 1,
        transform: 'scale(1)'
      }))
    ])
  ]);

  // Animation per lo zoom out
  static zoomOutAnimation: AnimationTriggerMetadata = trigger('zoomOutAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'scale(1.2)'
    })),
    transition('void => *', [
      animate('0.3s ease-out', style({
        opacity: 1,
        transform: 'scale(1)'
      }))
    ])
  ]);

  // Animation per il flip
  static flipAnimation: AnimationTriggerMetadata = trigger('flipAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'rotateY(90deg)'
    })),
    transition('void => *', [
      animate('0.6s ease-out', style({
        opacity: 1,
        transform: 'rotateY(0deg)'
      }))
    ])
  ]);

  // Animation per il pulse
  static pulseAnimation: AnimationTriggerMetadata = trigger('pulseAnimation', [
    state('active', style({
      transform: 'scale(1.05)'
    })),
    state('inactive', style({
      transform: 'scale(1)'
    })),
    transition('inactive => active', [
      animate('0.2s ease-out')
    ]),
    transition('active => inactive', [
      animate('0.2s ease-in')
    ])
  ]);

  // Animation per lo shake
  static shakeAnimation: AnimationTriggerMetadata = trigger('shakeAnimation', [
    transition('* => active', [
      animate('0.5s', style({ transform: 'translateX(0)' })),
      animate('0.1s', style({ transform: 'translateX(-10px)' })),
      animate('0.1s', style({ transform: 'translateX(10px)' })),
      animate('0.1s', style({ transform: 'translateX(-10px)' })),
      animate('0.1s', style({ transform: 'translateX(10px)' })),
      animate('0.1s', style({ transform: 'translateX(-10px)' })),
      animate('0.1s', style({ transform: 'translateX(10px)' })),
      animate('0.1s', style({ transform: 'translateX(0)' }))
    ])
  ]);

  // Animation per il bounce
  static bounceAnimation: AnimationTriggerMetadata = trigger('bounceAnimation', [
    state('void', style({
      opacity: 0,
      transform: 'translateY(-100px)'
    })),
    transition('void => *', [
      animate('0.5s ease-out', style({
        opacity: 1,
        transform: 'translateY(0)'
      }))
    ])
  ]);

  // Animation per il ripple effect
  static rippleAnimation: AnimationTriggerMetadata = trigger('rippleAnimation', [
    transition('* => active', [
      animate('0.6s', style({
        transform: 'scale(1.1)'
      })),
      animate('0.6s', style({
        transform: 'scale(1)'
      }))
    ])
  ]);

  // Animation per il glow effect
  static glowAnimation: AnimationTriggerMetadata = trigger('glowAnimation', [
    state('active', style({
      'box-shadow': '0 0 15px rgba(52, 152, 219, 0.5)'
    })),
    state('inactive', style({
      'box-shadow': 'none'
    })),
    transition('inactive => active', [
      animate('0.3s ease-out')
    ]),
    transition('active => inactive', [
      animate('0.3s ease-in')
    ])
  ]);

  // Funzione per generare animazioni con delay
  static staggerAnimation(items: any[], animationName: string, delay: number = 100): any[] {
    return items.map((item, index) => ({
      ...item,
      [animationName]: `${animationName} ${index * delay}ms`
    }));
  }

  // Funzione per generare animazioni casuali
  static randomAnimation(): string {
    const animations = [
      'fadeInOutAnimation',
      'slideInLeftAnimation',
      'slideInRightAnimation',
      'slideInTopAnimation',
      'slideInBottomAnimation',
      'zoomInAnimation',
      'zoomOutAnimation',
      'flipAnimation',
      'bounceAnimation'
    ];
    
    return animations[Math.floor(Math.random() * animations.length)];
  }

  // Funzione per generare animazioni basate sul tipo di elemento
  static getAnimationForType(type: string): string {
    const animationMap: { [key: string]: string } = {
      'stats': 'statsCardAnimation',
      'quickAction': 'quickActionsAnimation',
      'tab': 'tabsAnimation',
      'progress': 'progressBarAnimation',
      'badge': 'badgeAnimation',
      'tooltip': 'tooltipAnimation',
      'loading': 'loadingAnimation',
      'error': 'errorAnimation',
      'success': 'successAnimation',
      'card': 'zoomInAnimation',
      'button': 'pulseAnimation',
      'notification': 'slideInRightAnimation',
      'modal': 'fadeInOutAnimation',
      'sidebar': 'slideInLeftAnimation',
      'header': 'slideInTopAnimation',
      'footer': 'slideInBottomAnimation'
    };
    
    return animationMap[type] || 'fadeInOutAnimation';
  }
}