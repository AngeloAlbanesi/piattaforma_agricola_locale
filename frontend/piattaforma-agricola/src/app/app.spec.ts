import { TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { CoreModule } from './core/core.module';
import { App } from './app';

describe('App', () => {
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                RouterModule.forRoot([]),
                CoreModule,
            ],
            declarations: [
                App
            ],
        }).compileComponents();
    });

    it('should create the app', () => {
        const fixture = TestBed.createComponent(App);
        const app = fixture.componentInstance;
        expect(app).toBeTruthy();
    });

    it('should render the shell layout', () => {
        const fixture = TestBed.createComponent(App);
        fixture.detectChanges();
        const compiled = fixture.nativeElement as HTMLElement;
        expect(compiled.querySelector('app-shell')).toBeTruthy();
    });
});
