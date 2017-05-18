package com.dabsquared.gitlabjenkins.trigger.handler.note;

import com.dabsquared.gitlabjenkins.cause.CauseData;
import com.dabsquared.gitlabjenkins.gitlab.hook.model.NoteHook;
import com.dabsquared.gitlabjenkins.trigger.exception.NoRevisionToBuildException;
import com.dabsquared.gitlabjenkins.trigger.filter.BranchFilter;
import com.dabsquared.gitlabjenkins.trigger.filter.MergeRequestLabelFilter;
import com.dabsquared.gitlabjenkins.trigger.handler.AbstractWebHookTriggerHandler;
import hudson.model.Job;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.RevisionParameterAction;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.dabsquared.gitlabjenkins.cause.CauseDataBuilder.causeData;
import static com.dabsquared.gitlabjenkins.trigger.handler.builder.generated.BuildStatusUpdateBuilder.buildStatusUpdate;

/**
 * @author Nikolay Ustinov
 */
class NoteHookTriggerHandlerImpl extends AbstractWebHookTriggerHandler<NoteHook> implements NoteHookTriggerHandler {

    private static final Logger LOGGER = Logger.getLogger(NoteHookTriggerHandlerImpl.class.getName());

    private final String noteRegex;

    NoteHookTriggerHandlerImpl(String noteRegex) {
        this.noteRegex = noteRegex;
    }

    @Override
    public void handle(Job<?, ?> job, NoteHook hook, boolean ciSkip, BranchFilter branchFilter, MergeRequestLabelFilter mergeRequestLabelFilter) {

        /* Fix by tnt4brain */
        boolean flag1;
        boolean flag2;


        flag1 = hook.getMergeRequest() != null && hook.getMergeRequest().getLabels() != null;
        flag2 = flag1 ? mergeRequestLabelFilter.isMergeRequestAllowed(hook.getMergeRequest().getLabels()) : true;

        if (isValidTriggerPhrase(hook.getObjectAttributes().getNote())
            && ( flag2 )) {
            super.handle(job, hook, ciSkip, branchFilter, mergeRequestLabelFilter);
        }
    }

    @Override
    protected boolean isCiSkip(NoteHook hook) {
        return hook.getMergeRequest() != null
                && hook.getMergeRequest().getDescription() != null
                && hook.getMergeRequest().getDescription().contains("[ci-skip]");
    }

    @Override
    protected String getTargetBranch(NoteHook hook) {
        return hook.getMergeRequest() == null ? null : hook.getMergeRequest().getTargetBranch();
    }

    @Override
    protected String getTriggerType() {
        return "note";
    }

    @Override
    protected CauseData retrieveCauseData(NoteHook hook) {
        if (hook.getMergeRequest() == null) {
        return causeData()
                .withActionType(CauseData.ActionType.NOTE)
                .withSourceProjectId(hook.getObjectAttributes().getProjectId())
                .withTargetProjectId(0)
                .withBranch("")
                .withSourceBranch("")
                .withUserName(hook.getCommit().getAuthor().getName())
                .withUserEmail(hook.getCommit().getAuthor().getEmail())
                .withSourceRepoHomepage(hook.getRepository().getHomepage())
                .withSourceRepoName(hook.getProject().getName())
                .withSourceNamespace(hook.getProject().getNamespace())
                .withSourceRepoUrl(hook.getProject().getUrl())
                .withSourceRepoSshUrl(hook.getProject().getSshUrl())
                .withSourceRepoHttpUrl(hook.getProject().getHttpUrl())
                .withMergeRequestTitle("")
                .withMergeRequestDescription("")
                .withMergeRequestId(0)
                .withMergeRequestIid(0)
                .withMergeRequestTargetProjectId(0)
                .withTargetBranch("")
                .withTargetRepoName("")
                .withTargetNamespace("")
                .withTargetRepoSshUrl("")
                .withTargetRepoHttpUrl("")
                .withTriggeredByUser(hook.getUser().getName())
                .withLastCommit(hook.getCommit().getId())
                .withTargetProjectUrl("")
                .withTriggerPhrase(hook.getObjectAttributes().getNote())
                .build();
        } else {
        return causeData()
                .withActionType(CauseData.ActionType.NOTE)
                .withSourceProjectId(hook.getMergeRequest().getSourceProjectId())
                .withTargetProjectId(hook.getMergeRequest().getTargetProjectId())
                .withBranch(hook.getMergeRequest().getSourceBranch())
                .withSourceBranch(hook.getMergeRequest().getSourceBranch())
                .withUserName(hook.getMergeRequest().getLastCommit().getAuthor().getName())
                .withUserEmail(hook.getMergeRequest().getLastCommit().getAuthor().getEmail())
                .withSourceRepoHomepage(hook.getMergeRequest().getSource().getHomepage())
                .withSourceRepoName(hook.getMergeRequest().getSource().getName())
                .withSourceNamespace(hook.getMergeRequest().getSource().getNamespace())
                .withSourceRepoUrl(hook.getMergeRequest().getSource().getUrl())
                .withSourceRepoSshUrl(hook.getMergeRequest().getSource().getSshUrl())
                .withSourceRepoHttpUrl(hook.getMergeRequest().getSource().getHttpUrl())
                .withMergeRequestTitle(hook.getMergeRequest().getTitle())
                .withMergeRequestDescription(hook.getMergeRequest().getDescription())
                .withMergeRequestId(hook.getMergeRequest().getId())
                .withMergeRequestIid(hook.getMergeRequest().getIid())
                .withMergeRequestTargetProjectId(hook.getMergeRequest().getTargetProjectId())
                .withTargetBranch(hook.getMergeRequest().getTargetBranch())
                .withTargetRepoName(hook.getMergeRequest().getTarget().getName())
                .withTargetNamespace(hook.getMergeRequest().getTarget().getNamespace())
                .withTargetRepoSshUrl(hook.getMergeRequest().getTarget().getSshUrl())
                .withTargetRepoHttpUrl(hook.getMergeRequest().getTarget().getHttpUrl())
                .withTriggeredByUser(hook.getMergeRequest().getLastCommit().getAuthor().getName())
                .withLastCommit(hook.getMergeRequest().getLastCommit().getId())
                .withTargetProjectUrl(hook.getMergeRequest().getTarget().getWebUrl())
                .withTriggerPhrase(hook.getObjectAttributes().getNote())
                .build();
    }
    }

    @Override
    protected RevisionParameterAction createRevisionParameter(NoteHook hook, GitSCM gitSCM) throws NoRevisionToBuildException {
        return new RevisionParameterAction(retrieveRevisionToBuild(hook), retrieveUrIish(hook));
    }

    @Override
    protected BuildStatusUpdate retrieveBuildStatusUpdate(NoteHook hook) {
        if (hook.getMergeRequest() != null
                && hook.getMergeRequest().getSourceProjectId() != null) {
        return buildStatusUpdate()
            .withProjectId(hook.getMergeRequest().getSourceProjectId())
            .withSha(hook.getMergeRequest().getLastCommit().getId())
            .withRef(hook.getMergeRequest().getSourceBranch())
            .build();
            }
        else {
        return buildStatusUpdate()
            .withProjectId(hook.getObjectAttributes().getProjectId())
            .withSha(hook.getCommit().getId())
            .withRef("")
            .build();
            }
    }

    private String retrieveRevisionToBuild(NoteHook hook) throws NoRevisionToBuildException {
        if (hook.getMergeRequest() != null
                && hook.getMergeRequest().getLastCommit() != null
                && hook.getMergeRequest().getLastCommit().getId() != null) {

            return hook.getMergeRequest().getLastCommit().getId();
        } 
        if (hook.getCommit() != null
                && hook.getCommit().getId() != null) {
            return hook.getCommit().getId();
        }
        else {
            throw new NoRevisionToBuildException();
        }
    }

    private boolean isValidTriggerPhrase(String note) {
        if (StringUtils.isEmpty(this.noteRegex)) {
            return false;
        }
        final Pattern pattern = Pattern.compile(this.noteRegex);
        return pattern.matcher(note).matches();
    }
}
