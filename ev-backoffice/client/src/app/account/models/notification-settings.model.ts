import { NotificationsSettingType } from './notifications-setting-type.enum';

export interface UnitConfig {
  type: NotificationsSettingType;
  preference: boolean;
  id: number;
}

export interface NotificationSettings {
  taskQueue: UnitConfig[];
  clientProgress: UnitConfig[];
}
