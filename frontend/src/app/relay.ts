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
}
