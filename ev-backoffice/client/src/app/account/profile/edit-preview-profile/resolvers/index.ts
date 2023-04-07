import { OrganizationResolverService } from './organization-resolver.service';
import { ProfileResolverService } from './profile-resolver.service';
import { MyReviewsResolverService } from './my-reviews-resolver.service';
import { MyRatingsResolverService } from './my-rating-resolver.service';

export const RESOLVERS = [
  OrganizationResolverService,
  ProfileResolverService,
  MyReviewsResolverService,
  MyRatingsResolverService,
];
