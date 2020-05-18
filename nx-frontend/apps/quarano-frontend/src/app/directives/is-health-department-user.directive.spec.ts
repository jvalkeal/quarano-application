/* tslint:disable:no-unused-variable */

import { TestBed, async } from '@angular/core/testing';
import { IsHealthDepartmentUserDirective } from './is-health-department-user.directive';
import { FooterComponent } from '../components/layout/footer/footer.component';
import { TemplateRef, ViewContainerRef } from '@angular/core';
import {UserService} from '../services/user.service';

describe('Directive: IsHealthDepartmentUser', () => {
  let viewContainerRef: ViewContainerRef;
  let templateRef: TemplateRef<any>;
  let userService: UserService;

  beforeEach(() => {
    viewContainerRef = {} as any;
    templateRef = {} as TemplateRef<any>;
    userService = {} as any;
  });

  it('should create an instance', () => {
    const directive = new IsHealthDepartmentUserDirective(viewContainerRef, templateRef, userService);
    expect(directive).toBeTruthy();
  });
});
