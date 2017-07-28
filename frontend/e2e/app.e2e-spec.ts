import { RaspHomePage } from './app.po';

describe('rasp-home App', () => {
  let page: RaspHomePage;

  beforeEach(() => {
    page = new RaspHomePage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!');
  });
});
