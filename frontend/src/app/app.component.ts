import {Component} from '@angular/core';
import {SendCommandService} from "./send-command.service";
import {Relay} from "./relay";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    constructor(public service: SendCommandService) {
    }

    public change(relay: Relay, e) {
        this.service.set(relay.id, !relay.state);
    }

}
