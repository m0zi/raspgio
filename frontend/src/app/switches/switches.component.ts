import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import 'rxjs/add/operator/map';

import {Relay, RelayResult} from '../relay';

@Component({
    selector: 'app-switches',
    templateUrl: './switches.component.html',
    styleUrls: ['./switches.component.css']
})
export class SwitchesComponent implements OnInit {

    // Toggles
    public relayLightCouch: Relay;
    public relayLightDinner: Relay;
    public relayLightPassage: Relay;

    // Switches
    public relayLightFireplace: Relay;
    public relayLightShelf: Relay;
    public relayOutletTV: Relay;
    public relayHeatingFireplace: Relay;

    private _url: string;

    constructor(private _http: HttpClient) {
        this._url = 'http://192.168.0.5:8000/';

        this.relayLightCouch = new Relay('links', 5);
        this.relayLightDinner = new Relay('links', 6);
        this.relayLightPassage = new Relay('links', 7);

        this.relayOutletTV = new Relay('rechts', 4);
        this.relayLightShelf = new Relay('rechts', 5);
        this.relayLightFireplace = new Relay('rechts', 6);
        this.relayHeatingFireplace = new Relay('rechts', 7);
    }

    public ngOnInit() {
        this.get(this.relayLightFireplace);
        this.get(this.relayLightShelf);
        this.get(this.relayOutletTV);
        this.get(this.relayHeatingFireplace);
    }

    public set(relay: Relay, state: boolean) {
        const url = this._url + relay.getUrlSwitch(state);

        this._http.get(url)
            .map((data) => <RelayResult>data)
            .subscribe(data => {
                relay.state = data.state;
            });
    }

    public toggle(relay: Relay) {
        const url = this._url + relay.getUrlToggle();

        this._http.get(url)
            .map((data) => <RelayResult>data)
            .subscribe(data => {
                relay.state = data.state;
            });
    }

    private get(relay: Relay) {
        const url = this._url + relay.getUrlState();

        this._http.get(url)
            .map((data) => <RelayResult>data)
            .subscribe(data => {
                relay.state = data.state;
            });
    }
}
