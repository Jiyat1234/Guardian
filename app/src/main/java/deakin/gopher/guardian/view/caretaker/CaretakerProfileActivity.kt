package deakin.gopher.guardian.view.caretaker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import deakin.gopher.guardian.R
import deakin.gopher.guardian.model.CaretakerProfile
import deakin.gopher.guardian.services.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CaretakerProfileActivity : AppCompatActivity() {

    private lateinit var caretakerImage: ImageView
    private lateinit var photoIcon: ImageView
    private lateinit var editButton: FloatingActionButton

    private lateinit var addressField: TextInputEditText
    private lateinit var dobField: TextInputEditText
    private lateinit var phoneField: TextInputEditText
    private lateinit var wardField: TextInputEditText
    private lateinit var medicareField: TextInputEditText

    private var isEditable = false
    private val PICK_IMAGE_REQUEST = 100

    private val caretakerId = "123456"
    private val email = "test@example.com"

    private var currentProfile: CaretakerProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caretakerprofile)

        caretakerImage = findViewById(R.id.caretakerImage)
        photoIcon = findViewById(R.id.photoIcon)
        editButton = findViewById(R.id.editButton)

        addressField = findViewById(R.id.addressField)
        dobField = findViewById(R.id.dobField)
        phoneField = findViewById(R.id.phoneField)
        wardField = findViewById(R.id.wardField)
        medicareField = findViewById(R.id.medicareField)

        // Disable editing by default
        setFieldsEditable(false)

        fetchCaretakerProfile(caretakerId)

        photoIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        editButton.setOnClickListener {
            if (isEditable) {
                saveCaretakerProfile()
            }
            toggleEditableFields()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            caretakerImage.setImageURI(imageUri)
            // Optional: upload image or save URI
        }
    }

    private fun toggleEditableFields() {
        isEditable = !isEditable
        setFieldsEditable(isEditable)
        editButton.setImageResource(
            if (isEditable) android.R.drawable.ic_menu_save else android.R.drawable.ic_menu_edit
        )
        Toast.makeText(this, if (isEditable) "Edit mode enabled" else "Edit mode disabled", Toast.LENGTH_SHORT).show()
    }

    private fun setFieldsEditable(editable: Boolean) {
        listOf(addressField, dobField, phoneField, wardField, medicareField).forEach {
            it.isEnabled = editable
        }
    }

    private fun fetchCaretakerProfile(caretakerId: String) {
        // Use the GET /caretaker?id=123456 endpoint
        ApiClient.apiService.getCaretakerProfile(caretakerId)
            .enqueue(object : Callback<CaretakerProfile> {
                override fun onResponse(call: Call<CaretakerProfile>, response: Response<CaretakerProfile>) {
                    if (response.isSuccessful) {
                        response.body()?.let { profile ->
                            currentProfile = profile
                            addressField.setText(profile.address ?: "")
                            dobField.setText(profile.dob ?: "")
                            phoneField.setText(profile.phone ?: "")
                            wardField.setText(profile.ward ?: "")
                            medicareField.setText(profile.medicare ?: "")
                        } ?: run {
                            Toast.makeText(this@CaretakerProfileActivity, "Empty profile received", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@CaretakerProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                        Log.e("ProfileAPI", "Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CaretakerProfile>, t: Throwable) {
                    Toast.makeText(this@CaretakerProfileActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("ProfileAPI", "Failure: ${t.message}", t)
                }
            })
    }

    private fun saveCaretakerProfile() {
        val updatedProfile = CaretakerProfile(
            id = caretakerId,
            email = currentProfile?.email ?: email,
            address = addressField.text.toString(),
            dob = dobField.text.toString(),
            phone = phoneField.text.toString(),
            ward = wardField.text.toString(),
            medicare = medicareField.text.toString()
        )

        // Use PUT /caretaker/{id} endpoint
        ApiClient.apiService.updateCaretakerProfile(caretakerId, updatedProfile)
            .enqueue(object : Callback<deakin.gopher.guardian.model.BaseModel> {
                override fun onResponse(
                    call: Call<deakin.gopher.guardian.model.BaseModel>,
                    response: Response<deakin.gopher.guardian.model.BaseModel>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CaretakerProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CaretakerProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        Log.e("UpdateAPI", "Response code: ${response.code()}, error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<deakin.gopher.guardian.model.BaseModel>, t: Throwable) {
                    Toast.makeText(this@CaretakerProfileActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("UpdateAPI", "Failure: ${t.message}", t)
                }
            })
    }
}
