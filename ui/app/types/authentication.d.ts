
export interface PrivilegedUserDetails {
  username: string;
}

export interface ExpirableToken {
  token: string;
  expiration: number;
}

export interface AuthenticationDetails {
  user: PrivilegedUserDetails;
  access: ExpirableToken;
  refresh: ExpirableToken;
}

export interface PasswordAuthenticationForm {
  username: string;
  password: string;
}

export interface TokenAuthenticationForm {
  token: string;
}
