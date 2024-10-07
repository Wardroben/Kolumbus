package ru.smalljinn.kolumbus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.smalljinn.kolumbus.data.repository.PlacesRepository
import ru.smalljinn.model.data.Image
import ru.smalljinn.model.data.Place
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    fun createNewPlace() {
        viewModelScope.launch {
            placesRepository.upsertPlace(
                Place.getInitPlace().copy(
                    title = "Aboba drane",
                    description = "I bought Durov and his family!",
                    creationDate = kotlinx.datetime.Clock.System.now(),
                    images = listOf(
                        Image(id = 0, url = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmedia.timeout.com%2Fimages%2F105237890%2F750%2F562%2Fimage.jpg&f=1&nofb=1&ipt=578ac80c93b68ec58d04c8aa9a1ae4e047a7531a6e39d611f2f52bfd7520ea1a&ipo=images"),
                        Image(id = 0, url = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmedia.cntraveler.com%2Fphotos%2F5b96919654bc755e8941fcd7%2F16%3A9%2Fw_1280%2Cc_limit%2FGettyImages-526986108.jpg&f=1&nofb=1&ipt=ffeef083c03361ba674868ccf23e081ba1a5e364fbf14d099d5b399d52895e98&ipo=images"),
                        Image(id = 0, url = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.pinimg.com%2Foriginals%2F3d%2Fb5%2Fbf%2F3db5bf45830bf82b7961979570ead032.jpg&f=1&nofb=1&ipt=bfb8c0601fc97abe382c51d3f71fd3e18a3761167877acae2dd285d0b9889bdc&ipo=images"),
                        Image(id = 0, url = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.canal26.com%2Fmedia%2Fimage%2F2023%2F01%2F17%2F530383.jpg&f=1&nofb=1&ipt=98351f74585ad74428f252a8ea584b9e2224763851660606ae25fd80515c1b32&ipo=images")
                    )
                )
            )
        }
    }
}