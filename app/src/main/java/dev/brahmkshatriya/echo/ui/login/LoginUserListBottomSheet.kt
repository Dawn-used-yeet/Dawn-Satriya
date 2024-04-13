package dev.brahmkshatriya.echo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import dev.brahmkshatriya.echo.R
import dev.brahmkshatriya.echo.databinding.ButtonExtensionBinding
import dev.brahmkshatriya.echo.databinding.DialogLoginUserListBinding
import dev.brahmkshatriya.echo.models.UserEntity.Companion.toEntity
import dev.brahmkshatriya.echo.ui.common.openFragment
import dev.brahmkshatriya.echo.utils.autoCleared
import dev.brahmkshatriya.echo.utils.load
import dev.brahmkshatriya.echo.utils.observe
import dev.brahmkshatriya.echo.viewmodels.LoginUserViewModel

class LoginUserListBottomSheet : BottomSheetDialogFragment() {

    var binding by autoCleared<DialogLoginUserListBinding>()
    val viewModel by activityViewModels<LoginUserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLoginUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.accountListLogin.isEnabled = false

        observe(viewModel.allUsers) { (client, list) ->
            binding.accountListLoading.root.isVisible = list == null
            binding.accountListToggleGroup.isVisible = list != null
            client ?: return@observe
            list ?: return@observe

            binding.addAccount.setOnClickListener {
                openFragment(LoginFragment.newInstance(client.metadata.id, client.metadata.name))
                dismiss()
            }
            binding.accountListToggleGroup.removeAllViews()
            val listener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, id, isChecked ->
                if (isChecked) {
                    val user = list[id]
                    binding.accountListLogin.isEnabled = true
                    binding.accountListLogin.setOnClickListener {
                        viewModel.setLoginUser(user.toEntity(client.metadata.id))
                        dismiss()
                    }
                }
            }
            binding.accountListToggleGroup.addOnButtonCheckedListener(listener)
            list.forEachIndexed { index, user ->
                val button = ButtonExtensionBinding.inflate(
                    layoutInflater, binding.accountListToggleGroup, false
                ).root
                button.text = user.name
                binding.accountListToggleGroup.addView(button)
                user.cover.load(button, R.drawable.ic_account_circle) { button.icon = it }
                button.id = index
            }
        }
    }

}