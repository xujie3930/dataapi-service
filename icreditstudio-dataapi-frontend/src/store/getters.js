const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  appEnv: state => state.app.appEnv,

  token: state => state?.user?.token,
  userInfo: state => state?.user?.userInfo,
  userId: state => state?.user?.userInfo?.id,
  orgId: state => state.user.userOrgId,
  roles: state => state.user.roles,
  roleId: state => state.user.roleId,
  workspaceId: state => state.user.workspaceId
}
export default getters
