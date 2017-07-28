import { TestBed, inject } from '@angular/core/testing';

import { SendCommandService } from './send-command.service';

describe('SendCommandService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SendCommandService]
    });
  });

  it('should be created', inject([SendCommandService], (service: SendCommandService) => {
    expect(service).toBeTruthy();
  }));
});
