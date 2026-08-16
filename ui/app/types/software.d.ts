import type {Identified} from "~/types/common";

export interface SoftwareUpdateRequest {
  name: string;
  display_name: string;
}

export interface SoftwareDataTokenless extends Identified {
  name: string;
  display_name: string;
  created_at: string;
}

export interface SoftwareDataTokenized extends SoftwareDataTokenless {
  token: string;
}
