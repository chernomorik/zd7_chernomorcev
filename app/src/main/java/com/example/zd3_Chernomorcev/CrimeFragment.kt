package com.example.zd3_Chernomorcev

import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.text.format.DateFormat
import androidx.fragment.app.FragmentManager
import com.example.zd3_pavina.R

import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME="DialogTime"
private const val REQUEST_DATE =0
private const val REQUEST_TIME=0
private const val DATE_FORMAT = "EEE, MMM, dd"
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var chooseButton: Button
    private lateinit var sendButton: Button


    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()

        val dateNow = Date()
        crime.mDate = dateNow

        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        sendButton =view.findViewById(R.id.crime_report) as Button
        chooseButton =view.findViewById(R.id.crime_suspect) as Button
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,
            savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner as LifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(
                senquence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {

            }

            override fun onTextChanged(
                senquence: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {
                crime.setTitle(senquence.toString())
            }

            override fun afterTextChanged(senquence: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)
        solvedCheckBox.apply {
            setOnCheckedChangeListener{_, isChecked ->
                crime.setSolved(isChecked) }
        }
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.mDate).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager() as FragmentManager, DIALOG_DATE)
            }
        }
        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(crime.mDate).apply{
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager() as FragmentManager, DIALOG_TIME)
            }
        }
        sendButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {intent ->
                val choserIntent = Intent.createChooser(intent, getString(R.string.sent_report))
                startActivity(choserIntent)
            }
        }
        chooseButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_DATE)
            }

        }
        var pickContact = Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI)
        var packageManager = activity?.packageManager
        if(packageManager?.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null){
            sendButton.isEnabled = false
        }

    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected(date: Date) {
        crime.mDate = date
        updateUI()
    }

    private fun updateUI() {
        titleField.setText(crime.mTitle)
        dateButton.text = crime.mDate.toString()
        solvedCheckBox.apply {
            isChecked = crime.mSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.mSuspect.isNotEmpty()){
            chooseButton.text=crime.mSuspect
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when{
            resultCode!= Activity.RESULT_OK -> return
            requestCode== REQUEST_CONTACT && data !=null ->{
                val contactUri: Uri? =data.data
                val queryFields=arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor =
                    contactUri?.let {
                        requireActivity().contentResolver.query(it,queryFields,null,
                            null)
                    }
                cursor?.use {
                    if (it.count==0){
                        return
                    }
                    it.moveToFirst()
                    val suspect=it.getString(0)
                    crime.mSuspect=suspect
                    crimeDetailViewModel.saveCrime(crime)
                    chooseButton.text=suspect
                }

            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.mSolved)
        {
            getString(R.string.crime_report_solved)
        }
        else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(
            DATE_FORMAT,
                crime.mDate).toString()
        val suspect = if (crime.mSuspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        }
        else {
            getString(R.string.crime_report_suspect, crime.mSuspect)
        }

        return getString(
            R.string.crime_report,
            crime.mTitle, dateString,
            solvedString, suspect)
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(
                    ARG_CRIME_ID,
                    crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}