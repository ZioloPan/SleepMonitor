import { Component, OnInit } from '@angular/core';
import { Chart, ChartConfiguration, ChartData, BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js';
import { SleepService } from '../../sleep';
import { BaseChartDirective } from 'ng2-charts';

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

@Component({
  selector: 'app-sleep-chart',
  templateUrl: './sleep-chart.html',
  styleUrls: ['./sleep-chart.scss'],
  standalone: true,
  imports: [BaseChartDirective]
})
export class SleepChart implements OnInit {
  public sleepChartData: ChartData<'bar'> = {
    labels: ['Sleep Phases'],
    datasets: []
  };

  public sleepChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    indexAxis: 'y',
    scales: {
      x: {
        display: false,
        max: 1,
        min: 0
      },
      y: {
        display: true,
        ticks: {
          callback: (value) => {
            const hour = this.timeLabels[value as number];
            return hour ? `${hour}:00` : '';
          }
        },
        grid: {
          display: false
        }
      }
    },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (context) => {
            const phase = ['Wake', 'NREM', 'REM'][context.datasetIndex % 3];
            return `${phase} at ${this.timeLabels[context.dataIndex]}:00`;
          }
        }
      }
    }
  };

  private timeLabels: string[] = [];
  private phaseColors = ['#00FF00', '#FFA500', '#0000FF']; // Wake, NREM, REM

  constructor(private sleepService: SleepService) {}

  ngOnInit(): void {
    const from = 1685000000;
    const to = 1685028799;

    this.sleepService.getSleepPhases(from, to).subscribe((data) => {
      this.processSleepData(data);
    });
  }

  private processSleepData(data: any[]): void {
    if (!data || data.length === 0) return;

    // Sort data by timestamp
    data.sort((a, b) => a.timestamp - b.timestamp);

    // Prepare time labels (hours)
    const firstHour = new Date(data[0].timestamp * 1000).getHours();
    const lastHour = new Date(data[data.length - 1].timestamp * 1000).getHours();

    // Generate all hours in range
    for (let hour = firstHour; hour <= lastHour; hour++) {
      this.timeLabels.push(hour.toString().padStart(2, '0'));
    }

    // Group data into sleep phases segments
    const segments: {phase: number, start: number, end: number}[] = [];
    let currentPhase = data[0].stage;
    let segmentStart = 0;

    for (let i = 1; i < data.length; i++) {
      if (data[i].stage !== currentPhase) {
        const startHour = new Date(data[segmentStart].timestamp * 1000).getHours();
        const endHour = new Date(data[i].timestamp * 1000).getHours();

        segments.push({
          phase: currentPhase,
          start: startHour - firstHour,
          end: endHour - firstHour
        });

        currentPhase = data[i].stage;
        segmentStart = i;
      }
    }

    // Add last segment
    segments.push({
      phase: currentPhase,
      start: new Date(data[segmentStart].timestamp * 1000).getHours() - firstHour,
      end: this.timeLabels.length - 1
    });

    // Create datasets for each segment
    this.sleepChartData.datasets = segments.map((segment, index) => {
      const dataArray = new Array(this.timeLabels.length).fill(0);

      // Fill the segment range with 1s
      for (let i = segment.start; i <= segment.end; i++) {
        dataArray[i] = 1;
      }

      return {
        label: ['Wake', 'NREM', 'REM'][segment.phase],
        data: dataArray,
        backgroundColor: this.phaseColors[segment.phase],
        borderWidth: 0,
        barPercentage: 1.0,
        categoryPercentage: 1.0,
        // Set segment position and height
        base: segment.start,
        height: segment.end - segment.start + 1
      };
    });

    // Reverse to show earliest at bottom
    this.timeLabels.reverse();
  }
}
