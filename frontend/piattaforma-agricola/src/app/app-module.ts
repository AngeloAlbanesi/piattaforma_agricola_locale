import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing-module';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { CoreModule } from './core/core.module';
import { App } from './app';
import { authTokenInterceptor } from './core/interceptors/auth-token.interceptor';

@NgModule({
    declarations: [App],
    imports: [BrowserModule, BrowserAnimationsModule, CoreModule, AppRoutingModule, MatChipsModule, MatIconModule],
    providers: [provideBrowserGlobalErrorListeners(), provideHttpClient(withInterceptors([authTokenInterceptor]))],
    bootstrap: [App]
})
export class AppModule { }
