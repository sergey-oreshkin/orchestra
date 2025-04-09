import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MenuItem } from './menu-item';
import { NgClass, NgFor } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-base-template',
  imports: [RouterOutlet, NgFor, RouterLink],
  templateUrl: './base-template.component.html',
  styleUrl: './base-template.component.scss',
})
export class BaseTemplateComponent implements OnInit {
  menuItems: MenuItem[] = [];
  ngOnInit(): void {
    this.menuItems = [
      {
        title: 'Воркеры',
        link: 'worker',
        active: true,
      },
      {
        title: 'Стратегии',
        link: 'strategy',
        active: false,
      },
      {
        title: 'Запуск стратегии',
        link: 'launch',
        active: false,
      },
      {
        title: 'Остановка стратегии',
        link: 'stop',
        active: false,
      },
    ];
  }
}
