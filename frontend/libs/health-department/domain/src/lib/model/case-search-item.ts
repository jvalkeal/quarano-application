import { HalResponse } from '@qro/shared/util-data-access';

export interface CaseSearchItem extends HalResponse {
  fistName: string;
  lastName: string;
  dateOfBirth: string;
}
