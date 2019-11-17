import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';

import { Relay, RelayResult } from '../relay';

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

    public relayRomiBedroom: Relay;
    public relayRomi: Relay;

    public relayMiriam: Relay;
    public relayBedroom: Relay;

    // Switches
    public relayOutletTV: Relay;

    private _url: string;

    constructor(private _http: HttpClient) {
        this._url = 'http://192.168.0.5:8000/';

        this.relayLightCouch = new Relay(6);
        this.relayLightDinner = new Relay(7);
        this.relayLightPassage = new Relay(3);

        this.relayOutletTV = new Relay(0);

        this.relayBedroom = new Relay(11);
        this.relayMiriam = new Relay(13);

        this.relayRomiBedroom = new Relay(15);
        this.relayRomi = new Relay(9);
    }

    public ngOnInit() {
        this.get(this.relayOutletTV);
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
