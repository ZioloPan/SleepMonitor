import { Routes } from '@angular/router';
import { SleepChart } from './components/sleep-chart/sleep-chart';
import { SleepInfo } from './components/sleep-info/sleep-info';
import { Alarm } from './components/alarm/alarm';

export const routes: Routes = [
  { path: '', redirectTo: 'sleep-chart', pathMatch: 'full' }, // Default route
  { path: 'sleep-chart', component: SleepChart },
  { path: 'sleep-info', component: SleepInfo },
  { path: 'alarm', component: Alarm },
];
