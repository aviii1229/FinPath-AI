import { apiClient, ApiResponse } from './apiClient';

export interface AuthResponseData {
  accessToken: string;
  token?: string;
  tokenType: string;
  expiresInSeconds?: number;
  userId?: string;
  id?: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export const authApi = {
  login: async (email: string, password: string): Promise<AuthResponseData> => {
    const res = await apiClient.post<AuthResponseData>('/auth/login', { email, password });
    const jwt = res.data.accessToken || res.data.token;
    if (jwt) {
      localStorage.setItem('finpath_token', jwt);
    }
    return res.data;
  },

  register: async (
    firstName: string,
    lastName: string,
    email: string,
    password: string,
    phoneNumber?: string
  ): Promise<AuthResponseData> => {
    const res = await apiClient.post<AuthResponseData>('/auth/register', {
      firstName,
      lastName,
      email,
      password,
      phoneNumber: phoneNumber || '+919876543210',
    });
    const jwt = res.data.accessToken || res.data.token;
    if (jwt) {
      localStorage.setItem('finpath_token', jwt);
    }
    return res.data;
  },

  getCurrentUser: async (): Promise<any> => {
    const res = await apiClient.get<any>('/users/me');
    return res.data;
  },

  logout: () => {
    localStorage.removeItem('finpath_token');
    localStorage.removeItem('finpath_user');
  },
};
