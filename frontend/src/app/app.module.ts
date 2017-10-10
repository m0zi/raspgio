import {ErrorHandler, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {IonicApp, IonicErrorHandler, IonicModule} from 'ionic-angular';

import {StatusBar} from '@ionic-native/status-bar';
import {HttpModule} from "@angular/http";
import {SplashScreen} from '@ionic-native/splash-screen';

import {MyApp} from './app.component';

import {TabsPage} from '../pages/tabs/tabs';
import {VideoPage} from "../pages/video/video";
import {SwitchesPage} from "../pages/switches/switches";

@NgModule({
    declarations: [
        MyApp,
        VideoPage,
        SwitchesPage,
        TabsPage
    ],
    imports: [
        BrowserModule,
        HttpModule,
        IonicModule.forRoot(MyApp)
    ],
    bootstrap: [IonicApp],
    entryComponents: [
        MyApp,
        VideoPage,
        SwitchesPage,
        TabsPage
    ],
    providers: [
        StatusBar,
        SplashScreen,
        {
            provide: ErrorHandler,
            useClass: IonicErrorHandler
        }
    ]
})
export class AppModule {
}
