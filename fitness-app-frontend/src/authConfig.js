export const authConfig = {
  clientId: 'oath2-clent',
  authorizationEndpoint: 'http://localhost:8180/realms/fitness-oath2/protocol/openid-connect/auth',
  tokenEndpoint: 'http://localhost:8180/realms/fitness-oath2/protocol/openid-connect/token',
  redirectUri: 'http://localhost:8989',
  scope: 'openid profile email offline_access',
  onRefreshTokenExpire: (event) => event.logIn(),
}