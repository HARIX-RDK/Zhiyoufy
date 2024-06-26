export interface NavigationItem {
  elIcon?: string;
  iconifyIcon?: string;
  title: string;
  needRoles?: string[];
  index: string;
  children?: NavigationItem[];
}
