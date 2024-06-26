import type { EmsEnvironment } from './EmsEnvironment';


export interface EmsEnvironmentFull extends EmsEnvironment {
  isOwner: boolean;
  isEditor: boolean;
}
