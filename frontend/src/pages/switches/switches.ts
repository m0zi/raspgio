import {Component} from '@angular/core';
import {Relay} from "./relay";
import {Http, Response} from "@angular/http";
import 'rxjs/add/operator/map';

@Component({
    selector: 'page-switches',
    templateUrl: 'switches.html',
})
export class SwitchesPage {

    public relays: Relay[] = [];
    private _url: string;

    constructor(private _http: Http) {
        this._url = "http://192.168.0.5:8000/";

        for (let i = 0; i < 16; i++) {
            this.relays.push(new Relay(i));
        }
    }

    public ionViewWillEnter() {
        // TODO: Load states
    }

    public allOff() {
        this._http.get(this._url + 'reset').subscribe(data => {
            console.log(data);

            for (let i = 0; i < 16; i++) {
                this.relays[i].state = false;
            }
        });
    }

    public allOn() {
        this._http.get(this._url + 'test').subscribe(data => {
            console.log(data);

            for (let i = 0; i < 16; i++) {
                this.relays[i].state = true;
            }
        });
    }

    public set (relay: Relay, state: boolean) {
        let url = this._url + relay.getUrl(state);

        return this._http.get(url).subscribe(data => {
            console.log(data);

            relay.state = state;
        });
    }

    private extractData(res: Response) {
        return res.json();
    }
}
