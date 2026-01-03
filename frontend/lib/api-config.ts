export const API_BASE_URL = 'http://localhost:3000';

export const API_ENDPOINTS = {
    LOGIN: `${API_BASE_URL}/auth/login`,
    REGISTER: `${API_BASE_URL}/auth/register`,
    USERS: `${API_BASE_URL}/users`,
    BOOKINGS: `${API_BASE_URL}/bookings`,
    PARTS: `${API_BASE_URL}/catalog/parts`,
    SERVICES: `${API_BASE_URL}/catalog/services`,
    CONFIG: `${API_BASE_URL}/catalog/config`,
};
