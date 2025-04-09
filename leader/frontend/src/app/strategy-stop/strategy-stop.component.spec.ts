import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StrategyStopComponent } from './strategy-stop.component';

describe('StrategyStopComponent', () => {
  let component: StrategyStopComponent;
  let fixture: ComponentFixture<StrategyStopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StrategyStopComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StrategyStopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
