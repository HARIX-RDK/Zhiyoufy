import type { NavigationItem } from '@/model/bo';

import { PermissionUtils } from './permission-utils';
import { defaultSettings } from '@/settings';

const title = defaultSettings.title;


export class NavigationUtils {
  static getPageTitle(pageTitle?: string): string {
    if (pageTitle) {
      return `${pageTitle} - ${title}`
    }

    return `${title}`
  }

  static processNavigationItems(navItemsFull: NavigationItem[]): NavigationItem[] {
    const navItems: NavigationItem[] = [];

    for (const level1Item of navItemsFull) {
      if (level1Item.needRoles && !PermissionUtils.hasOneOfRole(level1Item.needRoles)) {
        continue;
      }

      const newLevel1Item = {
        ...level1Item,
      };
      delete newLevel1Item.needRoles;

      if (level1Item.children) {
        newLevel1Item.children = [];

        for (const level2Item of level1Item.children) {
          if (level2Item.needRoles && !PermissionUtils.hasOneOfRole(level2Item.needRoles)) {
            continue;
          }

          const newLevel2Item = {
            ...level2Item,
          };
          delete newLevel2Item.needRoles;

          newLevel1Item.children.push(newLevel2Item);
        }

        if (newLevel1Item.children.length == 0) {
          throw new Error('children length shouldn\'t be 0');
        }
      }

      navItems.push(newLevel1Item);
    }

    return navItems;
  }

  static getRouteIndex(navItem: NavigationItem): string {
    return '';
  }
}
