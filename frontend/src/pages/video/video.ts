import {Component} from '@angular/core';

@Component({
    selector: 'page-video',
    templateUrl: 'video.html',
})
export class VideoPage {

    // for start and stopping
    private timeout: any;

    constructor() {
    }

    public ionViewWillEnter() {
        // TODO: Start capturing Screenshots
        console.log("Start");
    }

    public ionViewWillLeave(){
        // TODO Stop capturing Screenshots
        console.log("Stop");
    }


}
