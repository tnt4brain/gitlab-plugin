package com.dabsquared.gitlabjenkins.trigger.filter;

/**
 * @author Robin Müller
 */
class AllBranchesFilter implements BranchFilter {
    @Override
    public boolean isBranchAllowed(String sourceBranchName, String targetBranchName) {
        return true;
    }
}
