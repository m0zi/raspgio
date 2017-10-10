import {Component} from '@angular/core';
import {VideoPage} from "../video/video";
import {SwitchesPage} from "../switches/switches";

@Component({
    templateUrl: 'tabs.html'
})
export class TabsPage {

    tab1Root = VideoPage;
    tab2Root = SwitchesPage;

    constructor() {

    }
}
