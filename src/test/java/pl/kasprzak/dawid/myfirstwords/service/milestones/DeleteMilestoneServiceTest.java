package pl.kasprzak.dawid.myfirstwords.service.milestones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kasprzak.dawid.myfirstwords.exception.AdminMissingParentIDException;
import pl.kasprzak.dawid.myfirstwords.exception.MilestoneNotFoundException;
import pl.kasprzak.dawid.myfirstwords.repository.MilestonesRepository;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ChildEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.MilestoneEntity;
import pl.kasprzak.dawid.myfirstwords.repository.dao.ParentEntity;
import pl.kasprzak.dawid.myfirstwords.util.AuthorizationHelper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteMilestoneServiceTest {
    @Mock
    private AuthorizationHelper authorizationHelper;
    @Mock
    private MilestonesRepository milestonesRepository;
    @InjectMocks
    private DeleteMilestoneService deleteMilestoneService;

    private MilestoneEntity milestoneEntity;
    private ChildEntity childEntity;

    @BeforeEach
    void setUp() {

        milestoneEntity = new MilestoneEntity();
        milestoneEntity.setId(1L);

        childEntity = new ChildEntity();
        childEntity.setId(1L);
        milestoneEntity.setChild(childEntity);
    }

    /**
     * Unit test for the deleteMilestone method in DeleteMilestoneService.
     * This test verifies that the service correctly deletes a milestone for a specific child
     * when the request is made by the authenticated parent.
     * The test ensures that:
     * 1. The child is validated and authorized for the parent using the AuthorizationHelper.
     * 2. The MilestonesRepository is queried to find the milestone associated with the child.
     * 3. If the milestone exists, it is successfully deleted from the child's account.
     */
    @Test
    void when_deleteMilestone_then_milestoneShouldBeDeletedFromChildAccount() {
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity.getId())).thenReturn(Optional.of(milestoneEntity));

        deleteMilestoneService.deleteMilestone(childEntity.getId(), milestoneEntity.getId(), null);

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, times(1)).delete(milestoneEntity);
    }

    /**
     * Unit test for the deleteMilestone method in DeleteMilestoneService when the milestone is not found.
     * This test verifies that the service correctly throws a MilestoneNotFoundException
     * if the specified milestone for a given child does not exist.
     * The test ensures that:
     * 1. The child is validated and authorized for the parent using the AuthorizationHelper.
     * 2. The MilestonesRepository is queried to find the milestone associated with the child.
     * 3. If the milestone does not exist, a MilestoneNotFoundException is thrown with the appropriate error message.
     */
    @Test
    void when_deleteMilestoneAndMilestoneNotFound_then_throwMilestoneNotFoundException() {
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null)).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity.getId())).thenReturn(Optional.empty());

        MilestoneNotFoundException milestoneNotFoundException = assertThrows(MilestoneNotFoundException.class,
                () -> deleteMilestoneService.deleteMilestone(childEntity.getId(), milestoneEntity.getId(), null));

        assertEquals("Milestone not found", milestoneNotFoundException.getMessage());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, never()).delete(any());
    }

    /**
     * Unit test for the deleteMilestone method in DeleteMilestoneService when accessed by an administrator
     * with a provided parent ID.
     * This test verifies that the service correctly deletes a milestone for a given child when the request
     * is made by an administrator and the parent ID is provided.
     * The test ensures that:
     * 1. The child is validated and authorized for the administrator using the AuthorizationHelper
     * with the provided parent ID.
     * 2. The MilestonesRepository is queried to find the milestone associated with the child.
     * 3. The milestone is successfully deleted from the child's account if it exists.
     */
    @Test
    void when_adminDeletesMilestoneWithParentID_then_milestoneShouldBeDeleted() {
        ParentEntity parent = new ParentEntity();
        parent.setId(1L);

        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), parent.getId())).thenReturn(childEntity);
        when(milestonesRepository.findByChildIdAndId(childEntity.getId(), milestoneEntity.getId())).thenReturn(Optional.of(milestoneEntity));

        deleteMilestoneService.deleteMilestone(childEntity.getId(), milestoneEntity.getId(), parent.getId());
        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), parent.getId());
        verify(milestonesRepository, times(1)).delete(milestoneEntity);
    }

    /**
     * Unit test for the deleteMilestone method in DeleteMilestoneService when accessed by an administrator
     * without providing a parent ID.
     * This test verifies that an AdminMissingParentIDException is thrown when the administrator
     * tries to delete a milestone for a child without providing a parent ID.
     * The test ensures that:
     * 1. The child is not authorized because the parent ID is missing.
     * 2. The AdminMissingParentIDException is thrown with the appropriate message.
     * 3. The MilestoneRepository is never queried if the authorization fails due to a missing parent ID.
     */
    @Test
    void when_adminDeletesMilestoneWithoutParentID_then_adminMissingParentIDExceptionShouldBeThrown(){
        lenient().when(authorizationHelper.isAdmin()).thenReturn(true);
        when(authorizationHelper.validateAndAuthorizeForAdminOrParent(childEntity.getId(), null))
                .thenThrow(new AdminMissingParentIDException("Admin must provide a parentID to perform this operation."));

        AdminMissingParentIDException adminMissingParentIDException = assertThrows(AdminMissingParentIDException.class,
                () -> deleteMilestoneService.deleteMilestone(childEntity.getId(), milestoneEntity.getId(), null));

        assertEquals("Admin must provide a parentID to perform this operation.", adminMissingParentIDException.getMessage());

        verify(authorizationHelper, times(1)).validateAndAuthorizeForAdminOrParent(childEntity.getId(), null);
        verify(milestonesRepository, never()).delete(any(MilestoneEntity.class));
    }
}