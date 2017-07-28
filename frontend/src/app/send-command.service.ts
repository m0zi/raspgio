import {Injectable} from '@angular/core';
import {Relay} from "./relay";
import {HttpClient} from "@angular/common/http";

@Injectable()
export class SendCommandService {
    private url: string;
    private relays: Relay[] = [];

    constructor(private http: HttpClient) {
        this.url = "http://192.168.178.36:8000/";

        for (let i = 0; i < 16; i++) {
            this.relays.push(new Relay(i));
        }
    }

    public get(): Relay[] {
        return this.relays;
    }

    public allOff() {
        this.http.get(this.url + 'reset').subscribe(data => {
            console.log(data);

            for (let i = 0; i < 16; i++) {
                this.relays[i].state = false;
            }
        });
    }

    public allOn() {
        this.http.get(this.url + 'test').subscribe(data => {
            console.log(data);

            for (let i = 0; i < 16; i++) {
                this.relays[i].state = true;
            }
        });
    }

    public set(id: number, value: boolean) {
        let relay = this.relays[id];
        let url = this.url + relay.getModule() + "_" + id + "?state=" + (value ? "on" : "off");

        return this.http.get(url).subscribe(data => {
            console.log(data);

            relay.state = value;
        });
    }
}
