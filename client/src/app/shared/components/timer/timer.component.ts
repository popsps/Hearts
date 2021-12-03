import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subscription, timer} from "rxjs";

@Component({
  selector: 'app-timer',
  templateUrl: './timer.component.html',
  styleUrls: ['./timer.component.scss']
})
export class TimerComponent implements OnInit, OnDestroy{
  get myTimer(): number {
    return this._myTimer;
  }

  subscription: Subscription;

  @Input()
  set myTimer(value: number) {
    // this.subscription.unsubscribe();
    // this.subscription = timer(0, 1000).subscribe(t => this._myTimer = value - t);
    this._myTimer = value;
  }

  private _myTimer: number;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

}
