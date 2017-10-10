export class Relay {
    id: number;
    state: boolean;

    constructor(id: number) {
        this.id = id;
        this.state = false;
    }

    public getModule() {
        if (this.id < 8) {
            return "oben";
        } else {
            return "unten";
        }
    }

    public getUrl(state: boolean) {
        var number = this.id < 8 ? this.id + 1 : this.id + 1 - 8;

        return this.getModule() + "_" + number + "?state=" + (state ? "on" : "off");
    }
}
