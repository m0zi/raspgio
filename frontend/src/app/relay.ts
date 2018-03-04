export class Relay {
    private location: string;
    private index: number;
    public state: boolean;

    constructor(location: string, index: number) {
        this.location = location;
        this.index = index;
    }

    public getUrlToggle() {
        return this.location + '_' + this.index + '?action=toggle';
    }

    public getUrlState() {
        return this.location + '_' + this.index + '?action=get';
    }

    public getUrlSwitch(state: boolean) {
        return this.location + '_' + this.index + '?action=' + (state ? 'on' : 'off');
    }
}

export class RelayResult {

    public state: boolean;

}