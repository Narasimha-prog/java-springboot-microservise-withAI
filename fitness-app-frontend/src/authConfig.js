export const authConfig = {
  clientId: 'fitness',
  authorizationEndpoint: 'http://localhost:8180/realms/fitness/protocol/openid-connect/auth',
  tokenEndpoint: 'http://localhost:8180/realms/fitness/protocol/openid-connect/token',
  logoutEndpoint: 'http://localhost:8180/realms/fitness/protocol/openid-connect/logout',
  redirectUri: 'http://localhost:8989',
  scope: 'openid profile email offline_access',
  onRefreshTokenExpire: (event) => event.logIn(),
}