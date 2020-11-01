package com.dabsquared.gitlabjenkins.trigger.filter;

/**
 * @author Robin Müller
 */
public interface BranchFilter {

    boolean isBranchAllowed(String sourceBranchName, String targetBranchName);
}
