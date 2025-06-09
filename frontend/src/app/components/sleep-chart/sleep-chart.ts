import { Component, OnInit } from '@angular/core';
import { Chart, ChartConfiguration, ChartData, Filler, BarController, LineController, LineElement, BarElement, PointElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js';
import { SleepService } from '../../sleep';
import { ViewChild } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {MatNativeDateModule, MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';

Chart.register(Filler, BarController, LineController, LineElement, BarElement, PointElement, CategoryScale, LinearScale, Tooltip, Legend);

export interface Night {
  nightId: number;
  date: string;
}

export interface HeartRate {
  id: number;
  timestamp: number;
  heartRateValue: number;
  nightId: number;
}

@Component({
  selector: 'app-sleep-chart',
  templateUrl: './sleep-chart.html', // Add this line
  styleUrls: ['./sleep-chart.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatDatepickerModule, MatFormFieldModule, MatInputModule, MatNativeDateModule, BaseChartDirective, MatSelect, MatOption],
  providers: [DatePipe]
})

export class SleepChart implements OnInit {
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  public selectedNightId: number | null = null;
  public nights: Night[] = [];
  public heartRateChartData: {
    datasets: {
      borderColor: string;
      backgroundColor: string;
      tension: number;
      data: number[];
      label: string;
      fill: boolean;
    }[];
    labels: string[];
  } = {
    labels: [],
    datasets: [
      {
        label: 'Heart Rate',
        data: [],
        borderColor: '#FF0000',
        backgroundColor: 'rgba(255, 0, 0, 0.3)',
        fill: true,
        tension: 0.4
      }
    ]
  };

  public heartRateChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      x: {
        display: true,
        title: {
          display: true,
          text: 'Time'
        }
      },
      y: {
        display: true,
        title: {
          display: true,
          text: 'Heart Rate (bpm)'
        },
        min: 40,
        max: 200
      }
    },
    plugins: {
      legend: { display: true },
      tooltip: { enabled: true }
    }
  };

  public sleepPhases: { time: string; phase: string }[] = [];

  constructor(private sleepService: SleepService, private datePipe: DatePipe) {}

  ngOnInit(): void {
    this.fetchNights();
  }

  onNightChange(event: any): void {
    const nightId = event.value;
    if (nightId) {
      this.fetchDataForNight(nightId);
    }
  }

  private fetchNights(): void {
    this.sleepService.getNights().subscribe(
      (data: Night[]) => {
        this.nights = data;
        if (this.nights.length > 0) {
          this.selectedNightId = this.nights[0].nightId; // Default to the first night
          this.fetchDataForNight(this.selectedNightId);
        }
      },
      (error) => console.error('Error fetching nights:', error)
    );
  }

  private fetchDataForNight(nightId: number): void {
    this.sleepService.getSleepPhases(nightId).subscribe(
      (data) => this.processSleepData(data),
      (error) => console.error('Error fetching sleep phases data:', error)
    );

    this.sleepService.getHeartRateData(nightId).subscribe(
      (data) => this.processHeartRateData(data),
      (error) => console.error('Error fetching heart rate data:', error)
    );
  }

  getPhaseClass(phase: string): string {
    switch (phase.toLowerCase()) {
      case 'wake':
        return 'phase-wake';
      case 'nrem':
        return 'phase-nrem';
      case 'rem':
        return 'phase-rem';
      default:
        return '';
    }
  }

  private processSleepData(data: any[]): void {
    this.sleepPhases = data.map((entry) => {
      const date = new Date(entry.timestamp * 1000);
      const time = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
      return { time, phase: entry.stage };
    });
  }

  private processHeartRateData(data: HeartRate[]): void {
    if (!data || data.length === 0) {
      console.warn('No heart rate data available.');
      return;
    }

    data.sort((a, b) => a.timestamp - b.timestamp);

    const timeLabels = data.map((entry) => {
      const date = new Date(entry.timestamp * 1000);
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    });

    const heartRates = data.map((entry) => entry.heartRateValue);

    this.heartRateChartData.labels = timeLabels;
    this.heartRateChartData.datasets[0].data = heartRates;

    // Notify the chart of the data update
    this.chart?.update();
  }
}
