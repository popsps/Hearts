import {
  ChangeDetectionStrategy,
  Component,
  EmbeddedViewRef,
  Input,
  OnInit,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {MatSnackBar, MatSnackBarRef} from "@angular/material/snack-bar";

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.scss'],
})
export class ErrorDialogComponent implements OnInit {


  durationInSeconds = 5;
  @ViewChild('errorBar', {static: true}) errorBar!: TemplateRef<any>;
  @Input() message!: string;
  private _render!: boolean;
  @Input()
  set render(value: boolean) {
    console.log('set show', value);
    this._render = value;
    if (this._render) {
      this.openSnackBar();
    }
  }

  bar!: MatSnackBarRef<EmbeddedViewRef<any>>;

  constructor(private snackBar: MatSnackBar) {
  }

  openSnackBar() {
    // this.snackBar.open(this.message, 'x');
    console.log(this.errorBar);
    this.bar =
      this.snackBar.openFromTemplate(this.errorBar, {
        duration: this.durationInSeconds * 1000,
        panelClass: ['hearts-error']
      });
  }

  dismiss(): void {
    this.bar.dismiss();
  }

  ngOnInit(): void {
  }

}
