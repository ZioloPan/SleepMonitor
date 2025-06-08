import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SleepInfo } from './sleep-info';

describe('SleepInfo', () => {
  let component: SleepInfo;
  let fixture: ComponentFixture<SleepInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SleepInfo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SleepInfo);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
