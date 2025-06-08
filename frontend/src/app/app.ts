import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import {Navigation} from './components/navigation/navigation';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HttpClientModule, RouterLink, Navigation], // Dodaj HttpClientModule tutaj
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'frontend';
}
