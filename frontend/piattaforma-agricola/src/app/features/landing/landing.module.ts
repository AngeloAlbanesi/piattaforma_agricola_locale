import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LandingRoutingModule } from './landing-routing.module';
import { RouterModule } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';

@NgModule({
  imports: [
    CommonModule,
    LandingRoutingModule,
    RouterModule,
    LandingComponent
  ]
})
export class LandingModule { }