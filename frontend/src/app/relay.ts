export class Relay {
    private index: string;
    public state: boolean;

    constructor(index: number) {
        this.index = (index + '').padStart(2, '0');
    }

    public getUrlToggle() {
        return 'relais_' + this.index + '?action=toggle';
    }

    public getUrlState() {
        return 'relais_' + this.index + '?action=get';
    }

    public getUrlSwitch(state: boolean) {
        return 'relais_' + this.index + '?action=' + (state ? 'on' : 'off');
    }
}

export class RelayResult {

    public state: boolean;

}
