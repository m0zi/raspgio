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

  public relayRomiMain1: Relay;
  public relayRomiMain2: Relay;
  public relayRomiLed1: Relay;
  public relayRomiLed2: Relay;

  public relayMiriamMain1: Relay;
  public relayMiriamMain2: Relay;
  public relayMiriamLed1: Relay;
  public relayMiriamLed2: Relay;

  // Switches
  public relayLightFireplace: Relay;
  public relayLightShelf: Relay;
  public relayOutletTV: Relay;

  private _url: string;

  constructor(private _http: HttpClient) {
    this._url = 'http://192.168.0.5:8000/';

    this.relayLightCouch = new Relay('rechts', 1);
    this.relayLightDinner = new Relay('rechts', 0);
    this.relayLightPassage = new Relay('rechts', 4);

    this.relayOutletTV = new Relay('rechts', 7);
    this.relayLightShelf = new Relay('rechts', 2);
    this.relayLightFireplace = new Relay('rechts', 3);

    this.relayMiriamLed1 = new Relay('links', 3);
    this.relayMiriamLed2 = new Relay('links', 1);
    this.relayMiriamMain1 = new Relay('links', 2);
    this.relayMiriamMain2 = new Relay('links', 0);

    this.relayRomiLed1 = new Relay('links', 7);
    this.relayRomiLed2 = new Relay('links', 5);
    this.relayRomiMain1 = new Relay('links', 6);
    this.relayRomiMain2 = new Relay('links', 4);
  }

  public ngOnInit() {
    this.get(this.relayLightFireplace);
    this.get(this.relayLightShelf);
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
